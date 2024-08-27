package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearnerRecordService {

    private final ObjectCache<CourseRecord> cache;
    private final ILearnerRecordClient client;

    public void bustCourseRecordCache(String learnerId, String courseId) {
        String id = String.format("%s,%s", learnerId, courseId);
        cache.evict(id);
    }

    public void updateCourseRecordCache(CourseRecord courseRecord) {
        cache.put(courseRecord);
    }

    public List<CourseRecord> getCourseRecords(String learnerId, List<String> courseIds) {
        try {
            List<String> ids = courseIds.stream().map(c -> String.format("%s,%s", learnerId, c)).toList();
            CacheGetMultipleOp<CourseRecord> result = cache.getMultiple(ids);
            List<CourseRecord> courseRecords = result.getCacheHits();
            if (!result.getCacheMisses().isEmpty()) {
                List<String> missCourseIds = result.getCacheMisses().stream().map(id -> id.split(",")[1]).toList();
                client.getCourseRecords(learnerId, missCourseIds).forEach(courseRecord -> {
                    courseRecords.add(courseRecord);
                    cache.put(courseRecord);
                });
            }
            return courseRecords;
        } catch (Cache.ValueRetrievalException ex) {
            log.error("Failed to retrieve courses from cache, falling back to API");
            return client.getCourseRecords(learnerId, courseIds);
        }
    }

    public CourseRecord getCourseRecord(String learnerId, String courseId) {
        return getCourseRecords(learnerId, List.of(courseId)).stream().findFirst().orElse(null);
    }

    public CourseRecord updateCourseRecord(CourseRecord recordUpdates) {
        log.debug(String.format("Updating with course record %s", recordUpdates));
        return client.updateCourseRecord(recordUpdates);
    }

    public CourseRecord createCourseRecord(CourseRecord input) {
        CourseRecord courseRecord = client.createCourseRecord(input);
        cache.put(courseRecord);
        return courseRecord;
    }

}
