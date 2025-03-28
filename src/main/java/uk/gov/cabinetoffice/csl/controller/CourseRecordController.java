package uk.gov.cabinetoffice.csl.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.FetchCourseRecordParams;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.service.CourseRecordService;

@Slf4j
@RestController
@RequestMapping("course_records")
public class CourseRecordController {

    private final CourseRecordService courseRecordService;

    public CourseRecordController(CourseRecordService courseRecordService) {
        this.courseRecordService = courseRecordService;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CourseRecords getCourseRecords(@Valid FetchCourseRecordParams params) {
        return new CourseRecords(this.courseRecordService.getCourseRecords(params));
    }
}
