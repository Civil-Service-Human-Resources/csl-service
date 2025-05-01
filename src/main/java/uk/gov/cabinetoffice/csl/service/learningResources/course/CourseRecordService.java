package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;

import java.util.List;

@Service
public class CourseRecordService {

    private final CourseWithRecordService courseWithRecordService;
    private final CourseRecordFactory courseRecordFactory;

    public CourseRecordService(CourseWithRecordService courseWithRecordService, CourseRecordFactory courseRecordFactory) {
        this.courseWithRecordService = courseWithRecordService;
        this.courseRecordFactory = courseRecordFactory;
    }

    public CourseRecord getCourseRecord(String userId, String courseId) {
        List<CourseRecord> result = getCourseRecords(userId, List.of(courseId));
        return result.size() == 0 ? null : result.get(0);
    }

    public List<CourseRecord> getCourseRecords(String userId, List<String> courseIds) {
        List<CourseWithRecord> courseWithRecords;
        if (courseIds.isEmpty()) {
            courseWithRecords = courseWithRecordService.getAllForUser(userId);
        } else {
            List<LearnerRecordResourceId> courseRecordIds = courseIds.stream().map(
                    courseId -> new LearnerRecordResourceId(LearningResourceType.COURSE, userId, courseId)
            ).toList();
            courseWithRecords = courseWithRecordService.get(courseRecordIds);
        }
        return courseWithRecords.stream().map(courseRecordFactory::transformToCourseRecord).toList();
    }

}
