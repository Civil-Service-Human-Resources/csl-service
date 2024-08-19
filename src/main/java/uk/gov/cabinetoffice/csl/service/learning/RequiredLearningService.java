package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.UserDetailsService;

import java.util.List;

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
        List<CourseRecord> courseRecords = learnerRecordService.getCourseRecords(userId, courseIds);
        return requiredLearningFactory.buildDetailedLearning(requiredLearning, courseRecords, user);
    }
}
