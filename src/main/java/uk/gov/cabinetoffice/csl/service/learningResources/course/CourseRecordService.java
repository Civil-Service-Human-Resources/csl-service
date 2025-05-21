package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ILearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.TypedLearnerRecordResourceId;

import java.util.List;

@Service
public class CourseRecordService {

    private final CourseWithRecordService courseWithRecordService;
    private final CourseRecordFactory courseRecordFactory;

    public CourseRecordService(CourseWithRecordService courseWithRecordService, CourseRecordFactory courseRecordFactory) {
        this.courseWithRecordService = courseWithRecordService;
        this.courseRecordFactory = courseRecordFactory;
    }

    public List<CourseRecord> getCourseRecords(String userId, List<String> courseIds) {
        List<CourseWithRecord> courseWithRecords;
        if (courseIds.isEmpty()) {
            courseWithRecords = courseWithRecordService.getAllForUser(userId);
        } else {
            List<? extends ILearnerRecordResourceID> courseRecordIds = courseIds.stream().map(
                    courseId -> new TypedLearnerRecordResourceId(userId, courseId, LearningResourceType.COURSE)
            ).toList();
            courseWithRecords = courseWithRecordService.get(courseRecordIds);
        }
        return courseRecordFactory.transformToCourseRecords(courseWithRecords.stream()
                .filter(course -> course.getRecord() != null).toList());
    }

}
