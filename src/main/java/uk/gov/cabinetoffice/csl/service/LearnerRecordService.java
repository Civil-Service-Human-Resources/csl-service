package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk.BulkCreateOutput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;
import uk.gov.cabinetoffice.csl.service.learningResources.ILearnerRecordService;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearnerRecordService implements ILearnerRecordService<LearnerRecord> {

    private final LearnerRecordFactory learnerRecordFactory;
    private final ObjectCache<LearnerRecord> learnerRecordCache;
    private final ObjectCache<ModuleRecord> moduleRecordCache;
    private final LearnerRecordParameterFactory learnerRecordQueryFactory;
    private final LearnerRecordDataFactory learnerRecordDataFactory;
    private final ILearnerRecordClient client;

    public void bustModuleRecordCache(ModuleRecordResourceId moduleRecordId) {
        moduleRecordCache.evict(moduleRecordId.getAsString());
    }

    public void bustModuleRecordCache(List<ModuleRecordResourceId> moduleRecordIds) {
        moduleRecordIds.forEach(this::bustModuleRecordCache);
    }

    public LearnerRecord getLearnerRecord(LearnerRecordResourceId id) {
        List<LearnerRecord> records = getLearnerRecords(List.of(id));
        return records.isEmpty() ? null : records.get(0);
    }

    public Map<String, ModuleRecord> getModuleRecordsMap(List<ModuleRecordResourceId> moduleRecordIds) {
        return this.getModuleRecords(moduleRecordIds).stream().collect(Collectors.toMap(ModuleRecord::getCacheableId, mr -> mr));
    }

    public List<CourseRecord> getAllCourseRecords(String userId) {
        List<CourseRecord> courseRecords = client.getCourseRecordsForUser(userId);
        courseRecords.forEach(cache::put);
        return courseRecords;
    }

    public List<ModuleRecord> getModuleRecords(List<ModuleRecordResourceId> moduleRecordIds) {
        try {
            List<String> ids = moduleRecordIds.stream().map(LearnerRecordResourceId::getAsString).toList();
            CacheGetMultipleOp<ModuleRecord> result = moduleRecordCache.getMultiple(ids);
            List<ModuleRecord> moduleRecords = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                List<ModuleRecordResourceId> missingModuleRecordIds = new ArrayList<>();
                result.getCacheMisses().forEach(id -> {
                    String[] splitId = id.split(",");
                    missingModuleRecordIds.add(new ModuleRecordResourceId(splitId[0], splitId[1]));
                });
                client.getModuleRecords(missingModuleRecordIds).forEach(moduleRecord -> {
                    moduleRecords.add(moduleRecord);
                    moduleRecordCache.put(moduleRecord);
                });
            }
            return moduleRecords;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve module records from cache, falling back to API");
            return client.getModuleRecords(moduleRecordIds);
        }
    }

    public LearnerRecordData getLearnerRecordAsData(LearnerRecordResourceId id) {
        LearnerRecordData learnerRecordData = this.getLearnerRecordsAsData(List.of(id)).get(id.getAsString());
        if (learnerRecordData == null) {
            learnerRecordData = learnerRecordDataFactory.createNewRecordData(id);
        }
        return learnerRecordData;
    }

    public Map<String, LearnerRecordData> getLearnerRecordsAsData(List<LearnerRecordResourceId> ids) {
        return this.getLearnerRecords(ids).stream().collect(Collectors.toMap(lr -> lr.getLearnerRecordId().getAsString(), learnerRecordDataFactory::createRecordData));
    }

    public LearnerRecord getOrCreateLearnerRecord(LearnerRecordResourceId resourceId) {
        List<LearnerRecord> records = getLearnerRecords(List.of(resourceId));
        if (records.isEmpty()) {
            LearnerRecordDto dto = learnerRecordFactory.createLearnerRecordDto(resourceId, List.of());
            records = createLearnerRecords(List.of(dto));
        }
        return records.get(0);
    }

    public List<LearnerRecord> getLearnerRecords(List<LearnerRecordResourceId> ids) {
        try {
            List<String> stringIds = ids.stream().map(LearnerRecordResourceId::getAsString).toList();
            CacheGetMultipleOp<LearnerRecord> result = learnerRecordCache.getMultiple(stringIds);
            List<LearnerRecord> learnerRecords = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                Set<String> missingResourceIds = new HashSet<>();
                Set<String> missingLearnerIds = new HashSet<>();
                result.getCacheMisses().forEach(id -> {
                    String[] splitId = id.split(",");
                    missingResourceIds.add(splitId[1]);
                    missingLearnerIds.add(splitId[0]);
                });
                LearnerRecordQuery query = learnerRecordQueryFactory.getLearnerRecordQuery(missingLearnerIds, missingResourceIds);
                client.getLearnerRecords(query).forEach(learnerRecord -> {
                    learnerRecords.add(learnerRecord);
                    learnerRecordCache.put(learnerRecord);
                });
            }
            return learnerRecords;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve learner records from cache, falling back to API");
            LearnerRecordQuery query = learnerRecordQueryFactory.getLearnerRecordQuery(ids);
            return client.getLearnerRecords(query);
        }
    }

    public List<LearnerRecord> getLearnerRecords(String learnerId) {
        LearnerRecordQuery query = learnerRecordQueryFactory.getLearnerRecordQuery(learnerId);
        List<LearnerRecord> courseRecords = client.getLearnerRecords(query);
        courseRecords.forEach(learnerRecordCache::put);
        return courseRecords;
    }

    @Override
    public LearnerRecordDtoCollection processLearnerRecordUpdates(List<LearnerRecordData> learnerRecordData) {
        LearnerRecordDtoCollection newDtos = learnerRecordFactory.createDtosFromData(learnerRecordData);
        if (!newDtos.getNewRecords().isEmpty()) {
            createLearnerRecords(newDtos.getNewRecords());
        }
        if (!newDtos.getNewEvents().isEmpty()) {
            createLearnerRecordEvents(newDtos.getNewEvents());
        }
        return newDtos;
    }

    public List<ModuleRecord> processModuleRecordUpdates(List<ModuleRecord> newModuleRecords, List<ModuleRecord> updateModuleRecords) {
        List<ModuleRecord> result = new ArrayList<>();
        result.addAll(this.createModuleRecords(newModuleRecords));
        result.addAll(this.updateModuleRecords(updateModuleRecords));
        return result;
    }

    @Override
    public LearnerRecordDtoCollection processUpdates(LearnerRecordResults learnerRecordUpdates) {
        if (!learnerRecordUpdates.getLearnerRecordUpdates().isEmpty()) {
            LearnerRecordDtoCollection newDtos = processLearnerRecordUpdates(learnerRecordUpdates.getLearnerRecordUpdates());
        }
        if (!learnerRecordUpdates.getModuleRecordUpdates().isEmpty()) {
            List<ModuleRecord> updatedModules = processModuleRecordUpdates(learnerRecordUpdates.getNewModuleRecords(), learnerRecordUpdates.getModuleRecordUpdates());
        }
        return newDtos;
    }

    public List<ModuleRecord> createModuleRecords(List<ModuleRecord> newRecords) {
        return client.createModuleRecords(newRecords);
    }

    public List<LearnerRecord> createLearnerRecords(List<LearnerRecordDto> newLearnerRecords) {
        BulkCreateOutput<LearnerRecord, LearnerRecordDto> learnerRecords = client.createLearnerRecords(newLearnerRecords);
        learnerRecords.getSuccessfulResources().forEach(learnerRecordCache::put);
        return learnerRecords.getSuccessfulResources();
    }

    public List<LearnerRecordEvent> createLearnerRecordEvents(List<LearnerRecordEventDto> newLearnerRecordEvents) {
        return client.createLearnerRecordEvents(newLearnerRecordEvents).getSuccessfulResources();
    }

    public List<ModuleRecord> updateModuleRecords(List<ModuleRecord> updatedRecords) {
        List<ModuleRecord> results = new ArrayList<>();
        client.updateModuleRecords(updatedRecords)
                .forEach(mr -> {
                    log.debug(String.format("Updated module record %s ", mr));
                    moduleRecordCache.put(mr);
                    results.add(mr);
                });
        return results;
    }
}
