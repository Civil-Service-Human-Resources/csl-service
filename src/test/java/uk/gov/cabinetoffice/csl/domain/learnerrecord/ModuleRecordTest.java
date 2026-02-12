package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModuleRecordTest {

    private final LearningPeriod learningPeriod = new LearningPeriod(
            LocalDate.of(2024, 1, 1),
            LocalDate.of(2025, 1, 1)
    );

    @Test
    void getStateForLearningPeriodCompletedSameDay() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        assertEquals(State.NULL, moduleRecord.getStateForLearningPeriod(learningPeriod));
    }

    @Test
    void getStateForLearningPeriodCompleted() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(LocalDateTime.of(2024, 2, 1, 10, 0, 0));
        assertEquals(State.COMPLETED, moduleRecord.getStateForLearningPeriod(learningPeriod));
    }

    @Test
    void getStateForLearningPeriodInProgress() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(LocalDateTime.of(2023, 2, 1, 10, 0, 0));
        moduleRecord.setUpdatedAt(LocalDateTime.of(2024, 2, 1, 10, 0, 0));
        assertEquals(State.IN_PROGRESS, moduleRecord.getStateForLearningPeriod(learningPeriod));
    }

    @Test
    void getStateForLearningPeriodNotStarted() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);
        moduleRecord.setCompletionDate(LocalDateTime.of(2023, 2, 1, 10, 0, 0));
        moduleRecord.setUpdatedAt(LocalDateTime.of(2023, 2, 1, 10, 0, 0));
        assertEquals(State.NULL, moduleRecord.getStateForLearningPeriod(learningPeriod));
    }

    @Test
    void getStateForLearningPeriodNoLearningPeriod() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(State.COMPLETED);
        assertEquals(State.COMPLETED, moduleRecord.getStateForLearningPeriod(null));
    }
}
