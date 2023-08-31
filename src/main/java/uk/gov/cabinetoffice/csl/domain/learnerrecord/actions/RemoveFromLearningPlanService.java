package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.util.Collections;
import java.util.List;

@Component
public class RemoveFromLearningPlanService extends CourseActionService {

    public RemoveFromLearningPlanService(LearnerRecordService learnerRecordService) {
        super(learnerRecordService);
    }

    @Override
    public CourseRecordAction getType() {
        return CourseRecordAction.REMOVE_FROM_LEARNING_PLAN;
    }

    public CourseRecord updateCourseRecord(String learnerId, String courseId) {
        List<PatchOp> patches = Collections.singletonList(PatchOp.replacePatch("state", State.ARCHIVED.name()));
        return learnerRecordService.updateCourseRecord(learnerId, courseId, patches);
    }
}
