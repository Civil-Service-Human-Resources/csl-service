package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

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
        assertEquals(LocalDate.of(2022, 6, 10), result.getStartDate());
        assertEquals(LocalDate.of(2023, 6, 10), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithPastDueDate() {
        audience.setFrequency("P1Y");
        audience.setRequiredBy(LocalDate.of(2021, 5, 10));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(LocalDate.of(2022, 5, 10), result.getStartDate());
        assertEquals(LocalDate.of(2023, 5, 10), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithLeapYear() {
        Clock clock = Clock.fixed(Instant.parse("2023-02-25T10:00:00.000Z"), ZoneId.of("Europe/London"));
        LearningPeriodFactory learningPeriodFactory = new LearningPeriodFactory(clock);
        audience.setFrequency("P10D");
        audience.setRequiredBy(LocalDate.of(2023, 2, 24));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(LocalDate.of(2023, 2, 24), result.getStartDate());
        assertEquals(LocalDate.of(2023, 3, 6), result.getEndDate());
    }

    @Test
    public void testGenerateLearningPeriodWithNoFrequency() {
        audience.setFrequency(null);
        audience.setRequiredBy(LocalDate.of(2023, 6, 10));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);
        assertEquals(null, result.getStartDate());
        assertEquals(LocalDate.of(2023, 6, 10), result.getEndDate());
    }

    @Test
    public void testBuildLearningPeriodReturnsCorrectLearningPeriodIfRequiredLearningIsInTheFuture(){
        Clock clock = Clock.fixed(Instant.parse("2025-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
        LearningPeriodFactory learningPeriodFactory = new LearningPeriodFactory(clock);
        Audience audience = new Audience();
        audience.setFrequency("P1Y");
        audience.setRequiredBy(LocalDate.of(2026, 3, 31));
        LearningPeriod result = learningPeriodFactory.buildLearningPeriod(audience);

        assertEquals(LocalDate.of(2024, 3, 31), result.getStartDate());
        assertEquals(LocalDate.of(2025, 3, 31), result.getEndDate());
    }

}
