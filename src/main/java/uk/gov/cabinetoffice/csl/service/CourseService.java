package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordStatus;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordActionService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordUpdate;

import java.util.List;

@Service
@Slf4j
public class CourseService {

    private final LearnerRecordService learnerRecordService;
    private final CourseRecordActionService courseRecordActionService;

    public CourseService(LearnerRecordService learnerRecordService, CourseRecordActionService courseRecordActionService) {
        this.learnerRecordService = learnerRecordService;
        this.courseRecordActionService = courseRecordActionService;
    }

    public CourseResponse addToLearningPlan(String learnerId, String courseId) {
        return processCourseRecordAction(learnerId, courseId, courseRecordActionService.getAddToLearningPlanUpdate());
    }

    public CourseResponse removeFromLearningPlan(String learnerId, String courseId) {
        return processCourseRecordAction(learnerId, courseId, courseRecordActionService.getRemoveFromLearningPlanUpdate());
    }

    public CourseResponse removeFromSuggestions(String learnerId, String courseId) {
        return processCourseRecordAction(learnerId, courseId, courseRecordActionService.getRemoveFromSuggestionsUpdate());
    }

    private CourseResponse processCourseRecordAction(String learnerId, String courseId, CourseRecordUpdate update) {
        log.info(String.format("Applying update '%s' to course record for course '%s' and user '%s'",
                update.getName(), courseId, learnerId));
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            CourseRecordStatus courseRecordStatus = update.getCreateCourseRecordStatus();
            courseRecord = learnerRecordService.createCourseRecord(learnerId, courseId, courseRecordStatus);
        } else {
            List<PatchOp> patches = update.getUpdateCourseRecordPatches();
            courseRecord = learnerRecordService.updateCourseRecord(learnerId, courseId, patches);
        }
        return new CourseResponse(String.format("Successfully applied action '%s' to course record", update.getName()),
                courseRecord.getCourseTitle(), courseId);
    }
}
