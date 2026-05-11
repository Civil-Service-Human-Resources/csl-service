package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetSuggestedLearningParams;
import uk.gov.cabinetoffice.csl.controller.learning.model.SuggestedLearning;
import uk.gov.cabinetoffice.csl.controller.learning.model.SuggestedLearningSection;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseAudienceMetadataMap;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseStatus;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SuggestedLearningService {

    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearnerRecordService learnerRecordService;
    private final LearningPlanFactory learningPlanFactory;

    public SuggestedLearningService(LearningCatalogueService learningCatalogueService, UserDetailsService userDetailsService, LearnerRecordService learnerRecordService, LearningPlanFactory learningPlanFactory) {
        this.learningCatalogueService = learningCatalogueService;
        this.userDetailsService = userDetailsService;
        this.learnerRecordService = learnerRecordService;
        this.learningPlanFactory = learningPlanFactory;
    }

    public SuggestedLearning getSuggestedLearningForUser(String uid, GetSuggestedLearningParams params) {
        User user = userDetailsService.getUserWithUid(uid);
        LearnerRecordQuery query = LearnerRecordQuery.builder().learnerIds(Set.of(uid)).build();
        Collection<String> allCourseIds = learnerRecordService.getAllCourseIds(query);
        CourseAudienceMetadataMap courseAudienceMetadataMap = learningCatalogueService.getCourseAudienceMetadataMap();
        Map<String, Collection<String>> filteredCourseIds = courseAudienceMetadataMap.filterForUser(user, params.isExcludeLearningPlanCourses() ? allCourseIds : List.of());
        Map<String, Course> courseMap = learningCatalogueService.getCourses(filteredCourseIds.values().stream().flatMap(Collection::stream).toList())
                .stream().filter(c -> c.getStatus().equals(CourseStatus.PUBLISHED)).collect(Collectors.toMap(Course::getId, course -> course));

        LinkedList<SuggestedLearningSection> allSections = new LinkedList<>();
        filteredCourseIds.forEach((sectionName, courseIds) -> {
            Stream<LearningPlanCourse> courseStream = courseIds.stream()
                    .map(cId -> {
                        State state = allCourseIds.contains(cId) ? State.IN_PROGRESS : State.NULL;
                        return learningPlanFactory.getLearningPlanCourse(courseMap.get(cId), state);
                    })
                    .sorted(Comparator.comparing(LearningPlanCourse::getTitle, String::compareToIgnoreCase));
            if (params.getSize() != null) {
                courseStream = courseStream.limit(params.getSize());
            }
            Collection<LearningPlanCourse> courses = courseStream.toList();
            allSections.add(new SuggestedLearningSection(sectionName, courses));
        });
        return new SuggestedLearning(allSections);
    }
}
