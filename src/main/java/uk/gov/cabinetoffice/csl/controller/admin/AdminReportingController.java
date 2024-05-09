package uk.gov.cabinetoffice.csl.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.ReportService;

@Slf4j
@RestController
@RequestMapping("admin/reporting")
@AllArgsConstructor
public class AdminReportingController {

    private final ReportService reportService;

    @PostMapping(path = "/course-completions/generate-graph", produces = "application/json")
    @ResponseBody
    public CourseCompletionChart getCourseCompletions(@Valid @RequestBody GetCourseCompletionsParams params) {
        return reportService.getCourseCompletionsChart(params);
    }
}
