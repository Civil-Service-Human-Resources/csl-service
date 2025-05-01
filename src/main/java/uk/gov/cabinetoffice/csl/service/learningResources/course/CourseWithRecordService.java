package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ILearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseWithRecordService {
    private final LearnerRecordService learnerRecordService;
    private final LearningCatalogueService learningCatalogueService;
    private final CourseWithRecordFactory courseWithRecordFactory;

    public CourseWithRecordService(LearnerRecordService learnerRecordService, LearningCatalogueService learningCatalogueService,
                                   CourseWithRecordFactory courseWithRecordFactory) {
        this.learnerRecordService = learnerRecordService;
        this.learningCatalogueService = learningCatalogueService;
        this.courseWithRecordFactory = courseWithRecordFactory;
    }

    public List<CourseWithRecord> getAllForUser(String userId) {
        Map<String, ILearnerRecord> courseRecordMap = learnerRecordService.getLearnerRecords(userId)
                .stream().collect(Collectors.toMap(ILearnerRecord::getResourceId, r -> r));
        List<Course> courses = learningCatalogueService.getCourses(courseRecordMap.keySet().stream().toList());
        return courses.stream().map(c -> courseWithRecordFactory.build(c, userId, courseRecordMap.get(c.getResourceId()))).toList();
    }

    public List<CourseWithRecord> get(List<LearnerRecordResourceId> courseRecordIds) {
        Map<String, ILearnerRecord> courseRecordMap = learnerRecordService.getLearnerRecords(courseRecordIds)
                .stream().collect(Collectors.toMap(ILearnerRecord::getResourceId, r -> r));
        Map<String, Course> courses = learningCatalogueService.getCourses(courseRecordMap.keySet())
                .stream().collect(Collectors.toMap(Course::getResourceId, c -> c));
        List<CourseWithRecord> list = new ArrayList<>();
        courseRecordIds.forEach(id -> list.add(courseWithRecordFactory.build(courses.get(id.getResourceId()), id.getLearnerId(), courseRecordMap.get(id.getResourceId()))));
        return list;
    }

    public CourseWithRecord get(LearnerRecordResourceId courseRecordId) {
        List<CourseWithRecord> courseWithRecords = get(List.of(courseRecordId));
        return courseWithRecords.isEmpty() ? null : courseWithRecords.get(0);
    }

}
