package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.util.List;

@Component
public class AddToLearningPlanService extends CourseActionService {

    public AddToLearningPlanService(LearnerRecordService learnerRecordService) {
        super(learnerRecordService);
    }

    @Override
    public CourseRecordAction getType() {
        return CourseRecordAction.ADD_TO_LEARNING_PLAN;
    }

    public CourseRecord updateCourseRecord(String learnerId, String courseId) {
        List<PatchOp> patches = List.of(
                PatchOp.replacePatch("preference", Preference.LIKED.name()),
                PatchOp.removePatch("state")
        );
        return learnerRecordService.updateCourseRecord(learnerId, courseId, patches);
    }

    @Override
    public CourseRecord createCourseRecord(String learnerId, String courseId) {
        CourseRecordStatus status = CourseRecordStatus.builder().preference(Preference.LIKED.name()).build();
        return learnerRecordService.createCourseRecord(learnerId, courseId, status);
    }
}
