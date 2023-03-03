package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.*;

import java.util.ArrayList;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.processCourseAndModuleData;

@Slf4j
@Service
public class ModuleRollupService {

    private final LearnerRecordService learnerRecordService;

    public ModuleRollupService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public void processRusticiRollupData(RusticiRollupData rusticiRollupData) {
        log.debug("rusticiRollupData: {}", rusticiRollupData);
        String courseIdDotModuleId = rusticiRollupData.getCourse().getId();
        if(!courseIdDotModuleId.contains(".")) {
            log.error("Invalid rustici rollup data. \".\" is missing from course.id: {}", rusticiRollupData);
            return;
        }

        String[] courseIdDotModuleIdParts = courseIdDotModuleId.split("\\.");
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rusticiRollupData.getLearner().getId();

        ModuleRecordInput moduleRecordInput = new ModuleRecordInput();
        moduleRecordInput.setModuleId(moduleId);
        moduleRecordInput.setCourseId(courseId);
        moduleRecordInput.setUserId(learnerId);
        moduleRecordInput.setState(rusticiRollupData.getRegistrationCompletion());
        moduleRecordInput.setResult(rusticiRollupData.getRegistrationSuccess());
        moduleRecordInput.setUpdated(rusticiRollupData.getUpdated());
        moduleRecordInput.setCompletedDate(rusticiRollupData.getCompletedDate());

        CourseRecordInput courseRecordInput = new CourseRecordInput();
        courseRecordInput.setCourseId(courseId);
        courseRecordInput.setUserId(learnerId);
        courseRecordInput.setCourseTitle(rusticiRollupData.getCourse().getTitle());
        courseRecordInput.setModuleRecords(new ArrayList<>());
        courseRecordInput.getModuleRecords().add(moduleRecordInput);

        processCourseAndModuleData(learnerRecordService, courseRecordInput);
    }
}
