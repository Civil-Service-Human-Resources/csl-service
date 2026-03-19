package uk.gov.cabinetoffice.csl.controller.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cabinetoffice.csl.controller.model.UserLearningResponse;
import uk.gov.cabinetoffice.csl.service.learning.UserLearningService;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class UserLearningController {

    private final UserLearningService userLearningService;

    @GetMapping("/{uid}")
    public UserLearningResponse getOptionalLearningRecord(@PathVariable String uid,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        return userLearningService.getOptionalLearningRecord(uid, page, size);
    }
}
