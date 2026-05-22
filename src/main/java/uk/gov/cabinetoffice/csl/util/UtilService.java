package uk.gov.cabinetoffice.csl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UtilService implements IUtilService {

    private final Clock clock;

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @Override
    public LocalDateTime getNowDateTime() {
        return LocalDateTime.now(clock);
    }

    @Override
    public Long getDurationUntilTomorrow(TemporalUnit unit) {
        LocalDateTime now = getNowDateTime();
        LocalDateTime tomorrowStart = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, tomorrowStart).get(unit);
    }

    @Override
    public <T> List<List<T>> batchList(List<T> list, Integer batchSize) {
        return IntStream.iterate(0, i -> i + batchSize)
                .limit((int) Math.ceil((double) list.size() / batchSize))
                .mapToObj(i -> list.subList(i, Math.min(i + batchSize, list.size())))
                .collect(Collectors.toList());
    }

}
