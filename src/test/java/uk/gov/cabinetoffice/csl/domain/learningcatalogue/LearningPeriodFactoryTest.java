package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LearningPeriodFactoryTest {

    Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
    LearningPeriodFactory learningPeriodFactory = new LearningPeriodFactory(clock);
    private final Audience audience = new Audience();

    @Test
    public void testGenerateLearningPeriodWithYearFrequency() {
        audience.setFrequency("P1Y");
        audience.setRequiredBy(LocalDate.of(2023, 6, 10));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(LocalDate.of(2022, 6, 10).atTime(LocalTime.MAX), result.getStartDate());
        assertEquals(LocalDate.of(2023, 6, 10).atTime(LocalTime.MAX), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithPastDueDate() {
        audience.setFrequency("P1Y");
        audience.setRequiredBy(LocalDate.of(2021, 5, 10));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(LocalDate.of(2022, 5, 10).atTime(LocalTime.MAX), result.getStartDate());
        assertEquals(LocalDate.of(2023, 5, 10).atTime(LocalTime.MAX), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithLeapYear() {
        Clock clock = Clock.fixed(Instant.parse("2023-02-25T10:00:00.000Z"), ZoneId.of("Europe/London"));
        LearningPeriodFactory learningPeriodFactory = new LearningPeriodFactory(clock);
        audience.setFrequency("P10D");
        audience.setRequiredBy(LocalDate.of(2023, 2, 24));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(LocalDate.of(2023, 2, 24).atTime(LocalTime.MAX), result.getStartDate());
        assertEquals(LocalDate.of(2023, 3, 6).atTime(LocalTime.MAX), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithNoFrequency() {
        audience.setFrequency(null);
        audience.setRequiredBy(LocalDate.of(2023, 6, 10));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(LocalDate.EPOCH.atStartOfDay(), result.getStartDate());
        assertEquals(LocalDate.of(2023, 6, 10).atTime(LocalTime.MAX), result.getEndDate());
    }

}
