package uk.gov.cabinetoffice.csl.service.job;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.service.job.model.FailedMessagesResult;
import uk.gov.cabinetoffice.csl.service.job.model.JobResult;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.time.LocalDateTime;

@Slf4j
@Component
public class RetryFailedMessages {

    private final MessagingRetryService messagingRetryService;
    private final IUtilService utilService;
    private final boolean enabled;

    public RetryFailedMessages(MessagingRetryService messagingRetryService, IUtilService utilService,
                               @Value("${jobs.retry-failed-messages.enabled:true}") boolean enabled) {
        this.messagingRetryService = messagingRetryService;
        this.utilService = utilService;
        this.enabled = enabled;
    }

    @Scheduled(cron = "${jobs.retry-failed-messages.cron-schedule}")
    @SchedulerLock(name = "retryFailedMessagesJob", lockAtMostFor = "${jobs.retry-failed-messages.lock-at-most-for}")
    public void scheduleJob() {
        if (!enabled) {
            log.info("Job is not enabled");
            return;
        }
        runJob();
    }

    @SchedulerLock(name = "retryFailedMessagesJob", lockAtMostFor = "${jobs.retry-failed-messages.lock-at-most-for}")
    public JobResult<FailedMessagesResult> runJob() {
        LockAssert.assertLocked();
        LocalDateTime start = utilService.getNowDateTime();
        log.info("Starting retry failed messages job at {}", start);
        FailedMessagesResult result = messagingRetryService.processFailedMessages();
        LocalDateTime end = utilService.getNowDateTime();
        log.info("Finished retry failed messages job at {}", end);
        log.info("Results {}", result);
        return new JobResult<>(start, end, result);
    }

    public Integer getFailedMessagesCount() {
        return messagingRetryService.getFailedMessagesCount();
    }
}
