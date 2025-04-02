package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;

import java.util.List;

@Service
public class CourseRecordService {

    private final LearnerRecordService learnerRecordService;


    public CourseRecordService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public List<CourseRecord> getCourseRecords(String userId, List<String> courseIds) {
        if (courseIds.isEmpty()) {
            return learnerRecordService.getAllCourseRecords(userId);
        } else {
            List<CourseRecordId> courseRecordIds = courseIds.stream().map(
                    courseId -> new CourseRecordId(userId, courseId)
            ).toList();
            return learnerRecordService.getCourseRecords(courseRecordIds);
        }
    }
}
