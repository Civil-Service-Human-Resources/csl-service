package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseActionService;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.CourseRecordActionServiceFactory;

@Service
@Slf4j
public class CourseService {

    private final LearnerRecordService learnerRecordService;
    private final CourseRecordActionServiceFactory courseRecordActionServiceFactory;

    public CourseService(LearnerRecordService learnerRecordService, CourseRecordActionServiceFactory courseRecordActionServiceFactory) {
        this.learnerRecordService = learnerRecordService;
        this.courseRecordActionServiceFactory = courseRecordActionServiceFactory;
    }

    public CourseResponse processCourseRecordAction(String learnerId, String courseId, CourseRecordAction action) {
        log.info(String.format("Applying action '%s' to course record for course '%s' and user '%s'",
                action, courseId, learnerId));
        CourseActionService actionService = courseRecordActionServiceFactory.getService(action);
        CourseRecord courseRecord = learnerRecordService.getCourseRecord(learnerId, courseId);
        if (courseRecord == null) {
            courseRecord = actionService.createCourseRecord(learnerId, courseId);
        } else {
            courseRecord = actionService.updateCourseRecord(courseRecord);
        }
        return new CourseResponse(String.format("Successfully applied action '%s' to course record", action),
                courseRecord.getCourseTitle(), courseId);
    }
}
