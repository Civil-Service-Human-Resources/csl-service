package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ILearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseWithRecordService {
    private final LearnerRecordService learnerRecordService;
    private final LearningCatalogueService learningCatalogueService;
    private final CourseWithRecordFactory courseWithRecordFactory;

    public CourseWithRecordService(LearnerRecordService learnerRecordService, LearningCatalogueService learningCatalogueService, CourseWithRecordFactory courseWithRecordFactory) {
        this.learnerRecordService = learnerRecordService;
        this.learningCatalogueService = learningCatalogueService;
        this.courseWithRecordFactory = courseWithRecordFactory;
    }

    private List<CourseWithRecord> buildForUser(String userId, List<String> courseIds, Map<String, LearnerRecord> courseRecordMap) {
        List<Course> courses = learningCatalogueService.getCourses(courseIds);
        return courses.stream().map(c -> courseWithRecordFactory.build(userId, c, courseRecordMap.get(c.getId()))).toList();
    }

    private List<CourseWithRecord> buildForUser(String userId, Map<String, LearnerRecord> courseRecordMap) {
        return buildForUser(userId, courseRecordMap.keySet().stream().toList(), courseRecordMap);
    }

    public List<CourseWithRecord> getAllForUser(String userId) {
        Map<String, LearnerRecord> courseRecordMap = learnerRecordService.getLearnerRecords(userId)
                .stream().collect(Collectors.toMap(LearnerRecord::getResourceId, r -> r));
        return buildForUser(userId, courseRecordMap);
    }

    public List<CourseWithRecord> get(String userId, String... courseIds) {
        Map<String, LearnerRecord> courseRecordMap = learnerRecordService.getLearnerRecords(userId, courseIds)
                .stream().collect(Collectors.toMap(LearnerRecord::getResourceId, r -> r));
        return buildForUser(userId, Arrays.stream(courseIds).toList(), courseRecordMap);
    }

    public CourseWithRecord get(ILearnerRecordResourceID courseRecordId) {
        List<CourseWithRecord> courseWithRecords = get(courseRecordId.getLearnerId(), courseRecordId.getResourceId());
        return courseWithRecords.isEmpty() ? null : courseWithRecords.get(0);
    }

}
