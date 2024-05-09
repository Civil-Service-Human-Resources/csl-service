package uk.gov.cabinetoffice.csl.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.reportservice.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.ReportService;

@Slf4j
@RestController
@RequestMapping("admin/reporting")
@AllArgsConstructor
public class AdminReportingController {

    private final ReportService reportService;

    @GetMapping(path = "/course-completions", produces = "application/json")
    @ResponseBody
    public CourseCompletionChart getCourseCompletions(@Valid GetCourseCompletionsParams params) {
        return reportService.getCourseCompletionsChart(params);
    }
}
