package uk.gov.cabinetoffice.csl.controller.learnerRecord;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.GetSkillsLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.SearchLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordPagedResponse;
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
    public SkillsLearnerRecordResponse getSkillsLearnerRecords(@Valid GetSkillsLearnerRecordsParams params) {
        return service.getSkillsLearnerRecords(params);
    }

    @PostMapping("/search")
    public SkillsLearnerRecordPagedResponse getLearnerRecords(@RequestHeader("orgCode") String organisationCode,
                                                              @Valid @RequestBody SearchLearnerRecordsParams params,
                                                              Pageable pageableParams) {
        return service.searchForLearnerRecords(organisationCode, params, pageableParams);
    }

}
