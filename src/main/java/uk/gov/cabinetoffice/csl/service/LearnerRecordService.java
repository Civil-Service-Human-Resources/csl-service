package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.GetModuleRecordParams;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ILearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearnerRecordService {

    private final LearnerRecordDtoFactory learnerRecordFactory;
    private final ObjectCache<LearnerRecord> learnerRecordCache;
    private final ObjectCache<ModuleRecord> moduleRecordCache;
    private final LearnerRecordParameterFactory learnerRecordQueryFactory;
    private final ILearnerRecordClient client;

    public void bustModuleRecordCache(ITypedLearnerRecordResourceID... moduleRecordIds) {
        Arrays.stream(moduleRecordIds).forEach(id -> moduleRecordCache.evict(id.getAsString()));
    }

    public void bustLearnerRecordCache(ITypedLearnerRecordResourceID... learnerRecordIds) {
        Arrays.stream(learnerRecordIds).forEach(id -> bustLearnerRecordCache(id.getLearnerId(), id.getResourceId()));
    }

    public void bustLearnerRecordCache(String learnerId, String resourceId) {
        learnerRecordCache.evict(String.format("%s,%s", learnerId, resourceId));
    }

    public LearnerRecord getLearnerRecord(ILearnerRecordResourceID id) {
        List<LearnerRecord> records = getLearnerRecords(List.of(id));
        return records.isEmpty() ? null : records.get(0);
    }

    public Map<String, ModuleRecord> getModuleRecordsMap(List<ModuleRecordResourceId> moduleRecordIds) {
        return this.getModuleRecords(moduleRecordIds).stream().collect(Collectors.toMap(ModuleRecord::getLearnerRecordIdAsString, mr -> mr));
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
                GetModuleRecordParams query = learnerRecordQueryFactory.getModuleRecordParams(missingModuleRecordIds);
                client.getModuleRecords(query).forEach(moduleRecord -> {
                    moduleRecords.add(moduleRecord);
                    moduleRecordCache.put(moduleRecord);
                });
            }
            return moduleRecords;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve module records from cache, falling back to API");
            GetModuleRecordParams query = learnerRecordQueryFactory.getModuleRecordParams(moduleRecordIds);
            return client.getModuleRecords(query);
        }
    }

    public List<LearnerRecord> getLearnerRecords(String learnerId, String... resourceIds) {
        return getLearnerRecords(Arrays.stream(resourceIds).map(rId -> new LearnerRecordResourceId(learnerId, rId)).toList());
    }

    public List<LearnerRecord> getLearnerRecords(List<? extends ILearnerRecordResourceID> ids) {
        try {
            List<String> stringIds = ids.stream().map(ILearnerRecordResourceID::getAsString).toList();
            CacheGetMultipleOp<LearnerRecord> result = learnerRecordCache.getMultiple(stringIds);
            List<LearnerRecord> learnerRecords = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                List<LearnerRecordResourceId> missingIds = new ArrayList<>();
                result.getCacheMisses().forEach(id -> {
                    String[] splitId = id.split(",");
                    missingIds.add(new LearnerRecordResourceId(splitId[0], splitId[1]));
                });
                LearnerRecordQuery query = learnerRecordQueryFactory.getLearnerRecordQuery(missingIds);
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

    public LearnerRecordDtoCollection processLearnerRecordUpdates(LearnerRecordResults result) {
        return processLearnerRecordUpdates(result.getLearnerRecordUpdates());
    }

    public Map<String, ModuleRecord> applyModuleRecordUpdates(LearnerRecordResults result) {
        Map<String, ModuleRecord> map = new HashMap<>();
        List<ModuleRecord> creates = new ArrayList<>();
        List<ModuleRecord> updates = new ArrayList<>();
        result.getModuleRecordUpdates().forEach(mr -> {
            if (mr.isNewRecord()) {
                creates.add(mr);
            } else {
                updates.add(mr);
            }
        });
        if (!creates.isEmpty()) {
            map.putAll(createModuleRecords(creates).stream()
                    .collect(Collectors.toMap(ModuleRecord::getLearnerRecordIdAsString, mr -> mr)));
        }
        if (!updates.isEmpty()) {
            map.putAll(updateModuleRecords(updates).stream()
                    .collect(Collectors.toMap(ModuleRecord::getLearnerRecordIdAsString, mr -> mr)));
        }
        return map;
    }

    public List<ModuleRecord> createModuleRecords(List<ModuleRecord> newRecords) {
        return client.createModuleRecords(newRecords);
    }

    public List<LearnerRecord> createLearnerRecords(List<LearnerRecordDto> newLearnerRecords) {
        return client.createLearnerRecords(newLearnerRecords)
                .stream().peek(learnerRecordCache::put).toList();
    }

    public List<LearnerRecordEvent> createLearnerRecordEvents(List<LearnerRecordEventDto> newLearnerRecordEvents) {
        return client.createLearnerRecordEvents(newLearnerRecordEvents)
                .stream().peek(lre -> {
                    LearnerRecord lr = learnerRecordCache.get(lre.getResourceId().getAsString());
                    if (lr != null) {
                        lr.setLatestEvent(lre);
                        learnerRecordCache.put(lr);
                    }
                }).toList();
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
