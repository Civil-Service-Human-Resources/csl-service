package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetPopularCoursesParams;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetSuggestedLearningParams;
import uk.gov.cabinetoffice.csl.controller.learning.model.SuggestedLearning;
import uk.gov.cabinetoffice.csl.controller.learning.model.SuggestedLearningSection;
import uk.gov.cabinetoffice.csl.controller.model.PagedResults;
import uk.gov.cabinetoffice.csl.controller.model.Results;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.domain.learning.learningPlan.LearningPlanCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseAggregation;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.CourseAggregationResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.ICourseAggregation;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.ReportService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CSLCatalogueService {

    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearnerRecordService learnerRecordService;
    private final LearningPlanFactory learningPlanFactory;
    private final ReportService reportService;

    public CSLCatalogueService(LearningCatalogueService learningCatalogueService, UserDetailsService userDetailsService, LearnerRecordService learnerRecordService, LearningPlanFactory learningPlanFactory, ReportService reportService) {
        this.learningCatalogueService = learningCatalogueService;
        this.userDetailsService = userDetailsService;
        this.learnerRecordService = learnerRecordService;
        this.learningPlanFactory = learningPlanFactory;
        this.reportService = reportService;
    }

    public SuggestedLearning getSuggestedLearningForUser(String uid, GetSuggestedLearningParams params) {
        User user = userDetailsService.getUserWithUid(uid);
        Collection<String> allCourseIds = learnerRecordService.getAllCourseIds(LearnerRecordQuery.builder().learnerIds(Set.of(uid)).build());
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

    public PagedResults<LearningPlanCourse> getCoursesForLetter(String uid, String startsWith, Pageable pageableParams) {
        CourseSearchResults coursesForLetter = learningCatalogueService.getCoursesForLetter(startsWith, pageableParams);
        User user = userDetailsService.getUserWithUid(uid);
        List<String> requiredLearningIds = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());
        Collection<String> courseIds = learnerRecordService.getAllCourseIds(LearnerRecordQuery.builder().learnerIds(Set.of(uid))
                .resourceIds(coursesForLetter.getResults().stream().map(Course::getId).collect(Collectors.toSet())).build());
        courseIds.addAll(requiredLearningIds);
        LinkedList<LearningPlanCourse> orderedCourses = new LinkedList<>();
        coursesForLetter.getResults().forEach(c -> {
            State state = courseIds.contains(c.getId()) ? State.IN_PROGRESS : State.NULL;
            orderedCourses.add(learningPlanFactory.getLearningPlanCourse(c, state));
        });
        return new PagedResults<>(orderedCourses, coursesForLetter.getPage(), coursesForLetter.getSize(), coursesForLetter.getTotalResults().intValue());
    }

    private Results<LearningPlanCourse> getPopularCoursesForAreaOfWork(String uid, Collection<String> departmentCodes, GetPopularCoursesParams params, Integer areaOfWorkId) {
        List<String> requiredLearningIds = learningCatalogueService.getRequiredLearningIdsForDepartments(departmentCodes);
        CourseAggregationResponse<CourseAggregation> courseAggregationsForAreaOfWork = reportService.getCourseAggregationsForAreaOfWork(params.getFrom(), params.getTo(), areaOfWorkId, requiredLearningIds);
        Collection<String> courseIds = learnerRecordService.getAllCourseIds(LearnerRecordQuery.builder().learnerIds(Set.of(uid))
                .resourceIds(courseAggregationsForAreaOfWork.getAggregations().stream().map(ICourseAggregation::getCourseId).collect(Collectors.toSet())).build());
        Map<String, Course> courses = learningCatalogueService.getCourseIdToCourseMap(courseAggregationsForAreaOfWork.getAggregations().stream().map(ICourseAggregation::getCourseId).toList());
        LinkedList<LearningPlanCourse> orderedResults = new LinkedList<>();
        for (CourseAggregation aggregation : courseAggregationsForAreaOfWork.getAggregations()) {
            Optional.ofNullable(courses.get(aggregation.getCourseId()))
                    .ifPresent(c -> {
                        if (c.getStatus().equals(CourseStatus.PUBLISHED) && c.getVisibility().equals(CourseVisibility.PUBLIC)) {
                            State state = courseIds.contains(c.getId()) ? State.IN_PROGRESS : State.NULL;
                            orderedResults.add(learningPlanFactory.getLearningPlanCourse(c, state));
                        }
                    });
            if (orderedResults.size() == params.getMaxResults()) {
                break;
            }
        }
        return new Results<>(orderedResults);
    }

    public Results<LearningPlanCourse> getPopularCoursesForAreaOfWork(String uid, GetPopularCoursesParams params, Integer areaOfWorkId) {
        User user = userDetailsService.getUserWithUid(uid);
        return getPopularCoursesForAreaOfWork(uid, user.getDepartmentCodes(), params, areaOfWorkId);
    }

    public Results<LearningPlanCourse> getPopularCoursesForAreaOfWork(String uid, GetPopularCoursesParams params) {
        User user = userDetailsService.getUserWithUid(uid);
        return getPopularCoursesForAreaOfWork(uid, user.getDepartmentCodes(), params, user.getProfessionId());
    }
}
