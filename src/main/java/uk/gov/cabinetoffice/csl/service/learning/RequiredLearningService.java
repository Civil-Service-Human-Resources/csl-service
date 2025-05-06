package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.UserDetailsService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequiredLearningService {

    private final LearnerRecordService learnerRecordService;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearningFactory<RequiredLearningDisplayCourseFactory> requiredLearningFactory;

    public Learning getDetailedRequiredLearning(String userId) {
        User user = userDetailsService.getUserWithUid(userId);
        List<Course> requiredLearning = learningCatalogueService.getRequiredLearningForDepartments(user.getDepartmentCodes());
        List<String> courseIds = requiredLearning.stream().map(Course::getId).toList();
        Map<String, CourseRecord> courseRecords = learnerRecordService.getCourseRecords(courseIds.stream().map(
                courseId -> new CourseRecordId(userId, courseId)
        ).toList()).stream().collect(Collectors.toMap(CourseRecord::getCourseId, c -> c));
        return requiredLearningFactory.buildDetailedLearning(requiredLearning, courseRecords, user);
    }
}
