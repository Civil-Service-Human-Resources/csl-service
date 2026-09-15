package uk.gov.cabinetoffice.csl.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
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

    @Override
    public String generateUrlSlugFromString(String input, int maxLength) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String slug = input.trim()
                .replaceAll("&", " and ")
                .toLowerCase(Locale.ENGLISH);
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = Pattern.compile("\\p{M}+").matcher(slug).replaceAll("");
        slug = Pattern.compile("['’]").matcher(slug).replaceAll("");
        slug = Pattern.compile("[^a-z0-9\\-]").matcher(slug).replaceAll("-");
        slug = Pattern.compile("-+").matcher(slug).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "");
        if (slug.length() > maxLength && maxLength > 0) {
            slug = slug.substring(0, maxLength).replaceAll("-$", "");
        }

        return slug;
    }
}
