package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LearningPeriodFactory {

    private final Clock clock;

    public LearningPeriod buildLearningPeriod(Audience audience) {
        LocalDateTime requiredByAsDateTime = audience.getRequiredBy().atTime(LocalTime.MAX);
        Optional<Period> optionalPeriod = audience.getFrequencyAsPeriod();
        if (optionalPeriod.isPresent()) {
            Period frequencyPeriod = optionalPeriod.get();
            LocalDateTime now = LocalDateTime.now(clock);
            LocalDateTime endDate = requiredByAsDateTime;
            while (endDate.isBefore(now)) {
                endDate = endDate.plus(frequencyPeriod);
            }
            LocalDateTime startDate = endDate.minus(frequencyPeriod);
            return new LearningPeriod(startDate, endDate);
        } else {
            return new LearningPeriod(LocalDate.EPOCH.atStartOfDay(), requiredByAsDateTime);
        }
    }
}
