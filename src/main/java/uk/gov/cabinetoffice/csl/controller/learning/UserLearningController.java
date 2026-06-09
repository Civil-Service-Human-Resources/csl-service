package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.learning.model.GetOptionalLearningRecordParams;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningResponse;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.service.learning.UserLearningService;

import java.util.List;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class UserLearningController {

    private final UserLearningService userLearningService;

    @GetMapping("/{uid}")
    public UserLearningResponse getOptionalLearningRecord(@PathVariable String uid, GetOptionalLearningRecordParams params) {
        return userLearningService.getOptionalLearningRecord(uid, params);
    }

    @GetMapping("/detailed/{uid}")
    @ResponseBody
    public Learning getDetailedLearningForUser(@PathVariable String uid, @RequestParam List<String> courseIds) {
        return userLearningService.getDetailedLearning(uid, courseIds);
    }
}
