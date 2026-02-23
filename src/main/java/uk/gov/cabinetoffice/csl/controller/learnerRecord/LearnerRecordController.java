package uk.gov.cabinetoffice.csl.controller.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.GetSkillsLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordResponse;
import uk.gov.cabinetoffice.csl.service.skills.SkillsRecordService;

@Slf4j
@RestController
@RequestMapping("learner-records")
public class LearnerRecordController {

    private final SkillsRecordService service;

    public LearnerRecordController(SkillsRecordService service) {
        this.service = service;
    }

    @GetMapping("/skills")
    public SkillsLearnerRecordResponse getSkillsLearnerRecords(GetSkillsLearnerRecordsParams params) {
        return service.getSkillsLearnerRecords(params);
    }

}
