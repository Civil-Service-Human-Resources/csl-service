package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleOverview;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LearningOverviewService {
    private final LearnerRecordService learnerRecordService;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;

    public LearningOverviewService(LearnerRecordService learnerRecordService, LearningCatalogueService learningCatalogueService, UserDetailsService userDetailsService) {
        this.learnerRecordService = learnerRecordService;
        this.learningCatalogueService = learningCatalogueService;
        this.userDetailsService = userDetailsService;
    }

    public CourseOverview getCourseOverview(String courseId, String uid) {
        User user = userDetailsService.getUserWithUid(uid);
        Course course = learningCatalogueService.getCourse(courseId);
        Map<String, ModuleRecord> moduleRecordsMap = learnerRecordService.getModuleRecords(course.getModules().stream().map(module -> new ModuleRecordResourceId(uid, module.getId())).toList())
                .stream().collect(Collectors.toMap(ModuleRecord::getModuleId, mr -> mr));
        List<ModuleOverview> moduleOverviews = course.getModules().stream().map(m -> {
            State state = Optional.ofNullable(moduleRecordsMap.get(m.getResourceId()))
                    .map(moduleRecord -> course.getLearningPeriodForUser(user)
                            .map(moduleRecord::getStateForLearningPeriod).orElse(moduleRecord.getState())).orElse(State.NULL);
            return new ModuleOverview(m.getId(), m.getTitle(), m.getDescription(), !m.isOptional(), m.isAssociatedLearning(), m.getModuleType(),
                    m.getDurationInSeconds(), m.getCost().intValue(), state);
        }).toList();
        return new CourseOverview(course.getId(), course.getTitle(), course.getDescription(), course.getLearningOutcomes(),
                course.getCourseType(), course.getDurationInSeconds(), course.getTags(), course.getGrades(), course.getCost(), moduleOverviews);
    }
}
