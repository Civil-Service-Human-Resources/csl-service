package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.FetchCourseRecordParams;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;

import java.util.List;

@Service
public class CourseRecordService {

    private final LearnerRecordService learnerRecordService;


    public CourseRecordService(LearnerRecordService learnerRecordService) {
        this.learnerRecordService = learnerRecordService;
    }

    public List<CourseRecord> getCourseRecords(FetchCourseRecordParams params) {
        if (params.getCourseIds().isEmpty()) {
            return learnerRecordService.getAllCourseRecords(params.getUserId());
        } else {
            return learnerRecordService.getCourseRecords(params.getAsCourseRecordIds());
        }
    }
}
