package uk.gov.cabinetoffice.csl.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.GetCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddCourseCompletionReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.chart.CourseCompletionChart;
import uk.gov.cabinetoffice.csl.service.ReportService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;

@Slf4j
@RestController
@RequestMapping("admin/reporting")
@AllArgsConstructor
public class AdminReportingController {

    private final ReportService reportService;
    private final IUserAuthService userAuthService;

    @PostMapping(path = "/course-completions/generate-graph", produces = "application/json")
    @ResponseBody
    public CourseCompletionChart getCourseCompletions(@Valid @RequestBody GetCourseCompletionsParams params) {
        IdentityDto user = userAuthService.getIdentity();
        return reportService.getCourseCompletionsChart(params, user);
    }

    @PostMapping(path = "/course-completions/request-source-data", produces = "application/json")
    @ResponseBody
    public AddCourseCompletionReportRequestResponse requestSourceData(@Valid @RequestBody CreateReportRequestParams params) {
        return reportService.requestCourseCompletionsExport(params);
    }
}
