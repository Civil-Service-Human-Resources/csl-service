package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningPeriodFactory {

    private final Clock clock;

    public LearningPeriod buildLearningPeriod(Audience audience) {
        log.debug(String.format("Building learning period for audience with departments: %s", audience.getDepartments()));
        LocalDateTime requiredByAsDateTime = audience.getRequiredBy().atTime(LocalTime.MAX);
        log.debug(String.format("Required by: %s", requiredByAsDateTime));
        return audience.getFrequencyAsPeriod().map(frequency -> {
            log.debug(String.format("Frequency is: Years: %s | Months: %s | Days: %s", frequency.getYears(), frequency.getMonths(), frequency.getDays()));
            LocalDateTime now = LocalDateTime.now(clock);
            log.debug(String.format("Time now is: %s", now));
            LocalDateTime endDate = requiredByAsDateTime;
            while (endDate.isBefore(now)) {
                endDate = endDate.plus(frequency);
            }
            LocalDateTime startDate = endDate.minus(frequency);
            log.debug(String.format("Start date is %s, end date is %s", startDate, endDate));
            return new LearningPeriod(startDate, endDate);
        }).orElseGet(() -> {
            log.debug("No frequency data found, setting start date as EPOCH");
            return new LearningPeriod(LocalDate.EPOCH.atStartOfDay(), requiredByAsDateTime);
        });
    }
}
