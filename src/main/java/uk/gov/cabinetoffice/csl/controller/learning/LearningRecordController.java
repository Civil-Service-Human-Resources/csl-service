package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecord;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.learning.LearningRecordService;

@Slf4j
@RestController
@RequestMapping("learning/record")
public class LearningRecordController {

    private final LearningRecordService learningRecordService;
    private final IUserAuthService userAuthService;

    public LearningRecordController(LearningRecordService learningRecordService, IUserAuthService userAuthService) {
        this.learningRecordService = learningRecordService;
        this.userAuthService = userAuthService;
    }

    @GetMapping
    @ResponseBody
    public LearningRecord getLearningRecordForUser() {
        return learningRecordService.getLearningRecord(userAuthService.getUsername());
    }

}

