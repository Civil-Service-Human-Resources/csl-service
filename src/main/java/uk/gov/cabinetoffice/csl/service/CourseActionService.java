package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordData;
import uk.gov.cabinetoffice.csl.service.learningResources.ILearnerRecordService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseWithRecordService;

import java.util.List;

@Service
public class CourseActionService {

    private final ILearnerRecordService<LearnerRecord> learnerRecordService;
    private final CourseWithRecordService courseWithRecordService;
    private final LearnerRecordDataFactory learnerRecordDataFactory;
    private final ResponseFactory responseFactory;

    public CourseActionService(ILearnerRecordService<LearnerRecord> learnerRecordService, CourseWithRecordService courseWithRecordService,
                               LearnerRecordDataFactory learnerRecordDataFactory, ResponseFactory responseFactory) {
        this.learnerRecordService = learnerRecordService;
        this.courseWithRecordService = courseWithRecordService;
        this.learnerRecordDataFactory = learnerRecordDataFactory;
        this.responseFactory = responseFactory;
    }

    public CourseResponse performCourseAction(ActionWithId actionWithId) {
        CourseWithRecord course = courseWithRecordService.get(actionWithId.getResourceId());
        boolean success = true;
        LearnerRecordData courseRecord = course.getRecord() == null ? null : learnerRecordDataFactory.createRecordData(course.getRecord());
        if (courseRecord == null) {
            if (actionWithId.getAction().canCreateRecord()) {
                courseRecord = learnerRecordDataFactory.createNewRecordData(actionWithId);
            }
        } else {
            if (courseRecord.shouldAddEvent(actionWithId.getAction())) {
                courseRecord.addNewEvent(actionWithId.getAction());
            }
        }
        if (courseRecord != null) {
            success = learnerRecordService.processLearnerRecordUpdates(List.of(courseRecord)).containsId(actionWithId.getResourceId());
        }
        return responseFactory.buildCourseResponseFromAction(actionWithId, course, success);
    }
}
