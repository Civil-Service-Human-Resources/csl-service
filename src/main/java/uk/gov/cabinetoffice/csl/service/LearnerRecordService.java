package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearnerRecordService {

    private final ObjectCache<CourseRecord> cache;
    private final ILearnerRecordClient client;

    public void bustCourseRecordCache(CourseRecordId courseRecordId) {
        cache.evict(courseRecordId.getAsString());
    }

    public void bustCourseRecordCache(List<CourseRecordId> courseRecordIds) {
        courseRecordIds.forEach(this::bustCourseRecordCache);
    }

    public List<CourseRecord> getAllCourseRecords(String userId) {
        List<CourseRecord> courseRecords = client.getCourseRecordsForUser(userId);
        courseRecords.forEach(cache::put);
        return courseRecords;
    }

    public List<CourseRecord> getCourseRecords(List<CourseRecordId> courseRecordIds) {
        try {
            List<String> ids = courseRecordIds.stream().map(CourseRecordId::getAsString).toList();
            CacheGetMultipleOp<CourseRecord> result = cache.getMultiple(ids);
            List<CourseRecord> courseRecords = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                List<CourseRecordId> missingCourseRecordIds = new ArrayList<>();
                result.getCacheMisses().forEach(id -> {
                    String[] splitId = id.split(",");
                    missingCourseRecordIds.add(new CourseRecordId(splitId[0], splitId[1]));
                });
                client.getCourseRecords(missingCourseRecordIds).forEach(courseRecord -> {
                    courseRecords.add(courseRecord);
                    cache.put(courseRecord);
                });
            }
            return courseRecords;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve courses from cache, falling back to API");
            return client.getCourseRecords(courseRecordIds);
        }
    }

    public List<CourseRecord> createCourseRecords(List<CourseRecord> newRecords) {
        newRecords = client.createCourseRecords(newRecords);
        newRecords.forEach(cache::put);
        return newRecords;
    }

    public Map<String, CourseRecord> updateCourseRecords(Map<String, CourseRecord> existingRecords, Map<String, CourseRecord> updatedRecords) {
        client.updateCourseRecords(updatedRecords.values().stream().toList())
                .forEach(cr -> {
                    CourseRecord existing = existingRecords.get(cr.getId());
                    existing.update(cr);
                    existingRecords.put(cr.getId(), cr);
                    log.debug(String.format("Updated course record %s ", existing));
                    cache.put(existing);
                });
        return existingRecords;
    }
}
