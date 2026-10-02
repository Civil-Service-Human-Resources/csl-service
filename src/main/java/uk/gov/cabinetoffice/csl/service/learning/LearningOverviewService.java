package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.CourseRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.CourseOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.LearningPlan;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.ModuleOverviewCollection;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction.COMPLETE_COURSE;
import static uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction.REMOVE_FROM_LEARNING_PLAN;
import static uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseStatus.ARCHIVED;

@Service
public class LearningOverviewService {
    private final LearnerRecordService learnerRecordService;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final LearningOverviewFactory learningOverviewFactory;

    public LearningOverviewService(LearnerRecordService learnerRecordService, LearningCatalogueService learningCatalogueService, UserDetailsService userDetailsService, LearningOverviewFactory learningOverviewFactory) {
        this.learnerRecordService = learnerRecordService;
        this.learningCatalogueService = learningCatalogueService;
        this.userDetailsService = userDetailsService;
        this.learningOverviewFactory = learningOverviewFactory;
    }

    public CourseOverview getCourseOverview(String courseId, String uid) {
        Course course = learningCatalogueService.getCourse(courseId);
        LearningPlan learningPlan = LearningPlan.CANNOT_BE_ADDED_TO_LEARNING_PLAN;
        ModuleOverviewCollection collection = new ModuleOverviewCollection();
        if (!Objects.equals(ARCHIVED, course.getStatus())) {
            User user = userDetailsService.getUserWithUid(uid);
            Optional<LearningPeriod> optLp = course.getLearningPeriodForUser(user);
            Map<String, ModuleRecord> moduleRecordsMap = learnerRecordService.getModuleRecords(course.getModules().stream().map(module -> new ModuleRecordResourceId(uid, module.getId())).toList())
                    .stream().collect(Collectors.toMap(ModuleRecord::getModuleId, mr -> mr));
            collection = learningOverviewFactory.getModuleOverviews(course.getModules(), moduleRecordsMap, optLp);
            if (optLp.isEmpty() && !collection.isEmpty()) {
                learningPlan = getIsInLearningPlan(uid, courseId, collection.isHasFaceToFace());
            }
        }
        return new CourseOverview(course.getId(), course.getTitle(), course.getDescription(), course.getLearningOutcomes(), course.getStatus(), learningPlan,
                course.getCourseType(), course.getDurationInSeconds(), course.getLearningTags(), course.getGrades(), course.getAreasOfWork(), course.getCost(), collection,
                collection.getMandatoryCount());
    }

    private LearningPlan getIsInLearningPlan(String uid, String courseId, boolean hasFaceToFaceModule) {
        LearnerRecord learnerRecord = learnerRecordService.getLearnerRecord(new CourseRecordResourceId(uid, courseId));
        if (hasFaceToFaceModule) {
            return LearningPlan.CANNOT_BE_ADDED_TO_LEARNING_PLAN;
        }
        if (learnerRecord == null) {
            return LearningPlan.NOT_IN_LEARNING_PLAN;
        }
        return Optional.ofNullable(learnerRecord.getLatestEvent())
                .map(lre -> {
                    if (lre.getActionType().equals(REMOVE_FROM_LEARNING_PLAN)) {
                        return LearningPlan.NOT_IN_LEARNING_PLAN;
                    } else if (lre.getActionType().equals(COMPLETE_COURSE)) {
                        return LearningPlan.CANNOT_BE_ADDED_TO_LEARNING_PLAN;
                    } else {
                        return LearningPlan.IS_IN_LEARNING_PLAN;
                    }
                }).orElse(LearningPlan.IS_IN_LEARNING_PLAN);
    }

}
