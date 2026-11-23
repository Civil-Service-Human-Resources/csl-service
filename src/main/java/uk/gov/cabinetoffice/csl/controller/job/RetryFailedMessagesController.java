package uk.gov.cabinetoffice.csl.controller.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.service.job.RetryFailedMessages;
import uk.gov.cabinetoffice.csl.service.job.model.FailedMessagesResult;
import uk.gov.cabinetoffice.csl.service.job.model.JobResult;

@Slf4j
@RestController
@RequestMapping("/jobs/retry-failed-messages")
@RequiredArgsConstructor
public class RetryFailedMessagesController {

    private final RetryFailedMessages retryFailedMessages;

    @PostMapping("start")
    @ResponseBody
    public JobResult<FailedMessagesResult> startFailedMessagesJob() {
        return retryFailedMessages.runJob();
    }

    @GetMapping("count")
    @ResponseBody
    public Integer countFailedMessagesJob() {
        return retryFailedMessages.getFailedMessagesCount();
    }
}
