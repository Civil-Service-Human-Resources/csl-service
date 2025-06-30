package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.FetchCourseRecordParams;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learningResources.course.CourseRecordService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("course_records")
public class CourseRecordController {

    private final CourseRecordService courseRecordService;
    private final IUserAuthService userAuthService;

    public CourseRecordController(CourseRecordService courseRecordService, IUserAuthService userAuthService) {
        this.courseRecordService = courseRecordService;
        this.userAuthService = userAuthService;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CourseRecords getCourseRecordsForUser(@Valid FetchCourseRecordParams params) {
        String learnerId = userAuthService.getUsername();
        List<CourseRecord> courseRecords = this.courseRecordService.getCourseRecords(learnerId, params.getCourseIds());
        return new CourseRecords(courseRecords);
    }
}
