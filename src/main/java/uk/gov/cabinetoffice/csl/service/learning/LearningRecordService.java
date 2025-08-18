package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecord;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecordCourse;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.RequiredLearningRecord;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LearningRecordService {

    private final LearnerRecordDataUtils learnerRecordDataUtils;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;

    public LearningRecordService(LearnerRecordDataUtils learnerRecordDataUtils,
                                 LearningCatalogueService learningCatalogueService, UserDetailsService userDetailsService) {
        this.learnerRecordDataUtils = learnerRecordDataUtils;
        this.learningCatalogueService = learningCatalogueService;
        this.userDetailsService = userDetailsService;
    }

    public LearningRecord getLearningRecord(String uid) {
        User user = userDetailsService.getUserWithUid(uid);
        List<String> requiredLearningIds = learningCatalogueService.getRequiredLearningIdsForDepartments(user.getDepartmentCodes());
        Map<String, LocalDateTime> completionDates = learnerRecordDataUtils.getCompletionDatesForCourses(uid, null);
        List<LearningRecordCourse> requiredLearning = new ArrayList<>();
        List<LearningRecordCourse> otherLearning = new ArrayList<>();
        learningCatalogueService.getCourses(Stream.concat(requiredLearningIds.stream(), completionDates.keySet().stream()).collect(Collectors.toSet()))
                .forEach(course -> {
                    LocalDateTime completionDate = completionDates.get(course.getId());
                    if (completionDate != null) {
                        LearningRecordCourse learningRecordCourse = new LearningRecordCourse(course.getId(), course.getTitle(), course.getCourseType(), course.getDurationInSeconds(), completionDate);
                        course.getLearningPeriodForUser(user)
                                .ifPresentOrElse(learningPeriod -> {
                                    if (completionDate.isAfter(learningPeriod.getStartDateAsDateTime())) {
                                        requiredLearning.add(learningRecordCourse);
                                    }
                                }, () -> otherLearning.add(learningRecordCourse));
                    }
                });
        RequiredLearningRecord requiredLearningRecord = new RequiredLearningRecord(requiredLearning, requiredLearningIds.size());
        LearningRecord learningRecord = new LearningRecord(uid, requiredLearningRecord, otherLearning);
        learningRecord.sort();
        return learningRecord;
    }
}
