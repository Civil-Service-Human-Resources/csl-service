package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetOptionalLearningRecordParams;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningCourse;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.CourseRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordPagedResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.*;
import java.util.stream.Collectors;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;

@Service
@RequiredArgsConstructor
public class UserLearningService {

    private final UserDetailsService userDetailsService;
    private final LearningCatalogueService learningCatalogueService;
    private final LearnerRecordService learnerRecordService;
    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final CourseRecordService courseRecordService;
    private final LearningFactory learningFactory;

    private OtherLearningResult getRecords(LearnerRecordQuery query, GetOptionalLearningRecordParams params) {
        LearnerRecordPagedResponse response = learnerRecordService.getLearnerRecordPage(query, params.getPage(), params.getSize());
        Collection<LearnerRecord> records = response.getContent() == null ? List.of() : response.getContent();
        Collection<String> courseIds = records.stream().map(LearnerRecord::getResourceId).toList();
        Collection<Course> courses = learningCatalogueService.getCourses(courseIds);
        return new OtherLearningResult(records, courses, courseIds, response.getTotalElements());
    }

    private OtherLearningResult getSearchedRecords(LearnerRecordQuery query, GetOptionalLearningRecordParams params) {
        Collection<String> allLearningPlanCourseIds = learnerRecordService.getAllCourseIds(query);
        PagedResponse<Course> filteredLearningPlanCourses = learningCatalogueService.searchWithinCourses(allLearningPlanCourseIds, params.getQ(), params.getPage(), params.getSize());
        Collection<Course> courses = filteredLearningPlanCourses.getContent();
        Collection<String> courseIds = courses.stream().map(Course::getId).toList();
        List<CourseRecordResourceId> courseRecordIds = new ArrayList<>();
        for (String learnerId : query.getLearnerIds()) {
            courseRecordIds.addAll(courseIds.stream().map(id -> new CourseRecordResourceId(learnerId, id)).toList());
        }
        Collection<LearnerRecord> records = learnerRecordService.getLearnerRecords(courseRecordIds);
        return new OtherLearningResult(records, courses, courseIds, filteredLearningPlanCourses.getTotalElements());
    }

    public UserLearningResponse getOptionalLearningRecord(String uid, GetOptionalLearningRecordParams params) {
        User user = userDetailsService.getUserWithUid(uid);
        List<String> requiredLearningIds = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());

        LearnerRecordQuery query = LearnerRecordQuery.builder()
                .learnerIds(Set.of(uid))
                .notResourceIds(requiredLearningIds)
                .build();

        OtherLearningResult result = isNullOrEmpty(params.getQ()) ? getRecords(query, params) : getSearchedRecords(query, params);
        Map<String, ModuleRecordCollection> moduleRecordsForCourses = learnerRecordDataUtils.getModuleRecordsForCourses(uid, result.courses());
        Collection<UserLearningCourse> userCourses = learningFactory.buildUserLearning(result.records(), moduleRecordsForCourses, result.courses());

        return new UserLearningResponse(userCourses, params.getPage(), params.getSize(), result.totalElements());
    }

    public Learning getDetailedLearning(String uid, List<String> courseIds) {
        User user = userDetailsService.getUserWithUid(uid);
        List<Course> courses = learningCatalogueService.getCourses(courseIds);
        Map<String, CourseRecord> courseRecords = courseRecordService.getCourseRecords(uid, courseIds)
                .stream().collect(Collectors.toMap(CourseRecord::getCourseId, c -> c));
        return learningFactory.buildDetailedLearning(courses, courseRecords, user);
    }
}
