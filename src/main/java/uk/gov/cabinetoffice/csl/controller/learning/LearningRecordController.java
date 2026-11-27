package uk.gov.cabinetoffice.csl.controller.learning;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecord;
import uk.gov.cabinetoffice.csl.service.ReportService;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.LearningRecordService;

@RestController
@RequestMapping("learning/record")
public class LearningRecordController {

    private final LearningRecordService learningRecordService;
    private final IUserAuthService userAuthService;
    private final ReportService reportService;

    public LearningRecordController(LearningRecordService learningRecordService, IUserAuthService userAuthService, ReportService reportService) {
        this.learningRecordService = learningRecordService;
        this.userAuthService = userAuthService;
        this.reportService = reportService;
    }

    @GetMapping
    @ResponseBody
    public LearningRecord getLearningRecordForUser() {
        String loggedInUserId = userAuthService.getUsername();
        return learningRecordService.getLearningRecord(loggedInUserId);
    }

    @GetMapping(path = {"/export/{userId}", "/download/{userId}"})
    @ResponseBody
    public ResponseEntity<byte[]> getLearnerRecordExportForUserId(@PathVariable("userId") String userId) {
        return reportService.getLearnerRecordExportForUserId(userId);
    }

    @GetMapping(path = {"/export", "/download"})
    @ResponseBody
    public ResponseEntity<byte[]> getLearnerRecordExportForLoggedInUser() {
        return reportService.getLearnerRecordExportForLoggedInUser();
    }

}

