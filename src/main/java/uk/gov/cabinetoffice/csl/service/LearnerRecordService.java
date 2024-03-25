package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearnerRecordService {

    private final ILearnerRecordClient client;

    @CacheEvict(value = "course-record", key = "{#learnerId, #courseId}")
    public void bustCourseRecordCache(String learnerId, String courseId) {

    }

    @CachePut(value = "course-record", key = "{ #courseRecord.getUserId(), #courseRecord.getCourseId() }")
    public CourseRecord updateCourseRecordCache(CourseRecord courseRecord) {
        log.debug(String.format("Saving course record to cache: %s", courseRecord.toString()));
        return courseRecord;
    }

    @Cacheable(value = "course-record", key = "{#learnerId, #courseId}", unless = "#result == null")
    public CourseRecord getCourseRecord(String learnerId, String courseId) {
        CourseRecords courseRecords = client.getCourseRecord(learnerId, courseId);
        if (courseRecords == null) {
            return null;
        }
        return courseRecords.getCourseRecord(courseId);
    }

    public CourseRecord updateCourseRecord(CourseRecord input) {
        return client.updateCourseRecord(input);
    }

    public CourseRecord createCourseRecord(CourseRecord input) {
        return client.createCourseRecord(input);
    }

}
