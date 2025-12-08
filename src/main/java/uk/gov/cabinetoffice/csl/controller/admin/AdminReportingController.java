package uk.gov.cabinetoffice.csl.controller.admin;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithSelectedOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.RegisteredLearnerReportRequestParams;
import uk.gov.cabinetoffice.csl.controller.model.SelectedOrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;
import uk.gov.cabinetoffice.csl.domain.reportservice.AddReportRequestResponse;
import uk.gov.cabinetoffice.csl.domain.reportservice.RegisteredLearnerOverview;
import uk.gov.cabinetoffice.csl.domain.reportservice.ReportType;
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
    public CourseCompletionChart getCourseCompletions(@Valid @RequestBody SelectedOrganisationIdsCourseCompletionsParams params) {
        IdentityDto user = userAuthService.getIdentity();
        return reportService.getCourseCompletionsChart(params, user);
    }

    @PostMapping(path = "/course-completions/request-source-data", produces = "application/json")
    @ResponseBody
    public AddReportRequestResponse requestCourseCompletionsSourceData(@Valid @RequestBody CreateReportRequestWithSelectedOrganisationIdsParams params) {
        return reportService.requestCourseCompletionsExport(params);
    }

    @GetMapping(path = "/{reportTypeString}/download-report/{urlSlug}", produces = "application/octet-stream")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> requestSourceData(@PathVariable String reportTypeString, @PathVariable String urlSlug) {
        ReportType reportType = ReportType.getWithUrl(reportTypeString);
        if (reportType == null) {
            return ResponseEntity.notFound().build();
        }
        DownloadableFile file = reportService.downloadCourseCompletionsReport(reportType, urlSlug);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(file.getData());
    }

    @GetMapping(path = "/registered-learners/overview", produces = "application/json")
    @ResponseBody
    public RegisteredLearnerOverview getRegisteredLearnerOverview() {
        IdentityDto user = userAuthService.getIdentity();
        return reportService.getRegisteredLearnerOverview(user);
    }

    @PostMapping(path = "/registered-learners/request-source-data", produces = "application/json")
    @ResponseBody
    public AddReportRequestResponse requestRegisteredLearnerSourceData(@Valid @RequestBody RegisteredLearnerReportRequestParams params) {
        return reportService.requestRegisteredLearnerExport(params);
    }

}
