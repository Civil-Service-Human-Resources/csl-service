package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayModule;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecordCourse;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.cabinetoffice.csl.domain.learnerrecord.State.*;

class DisplayModuleFactoryTest {

    DisplayModuleFactory factory = new DisplayModuleFactory();

    private static final DisplayModule module1 = new DisplayModule("", "", "", "", true, true,
            null, null, State.NULL);
    private static final DisplayModule module2 = new DisplayModule("", "", "", "", true, true,
            LocalDateTime.now(), null, IN_PROGRESS);
    private static final DisplayModule module3 = new DisplayModule("", "", "", "", true, true,
            LocalDateTime.now(), LocalDateTime.now(), State.COMPLETED);
    private static final DisplayModule module4 = new DisplayModule("", "", "", "", false, false,
            null, null, IN_PROGRESS);
    private static final DisplayModule module5 = new DisplayModule("", "", "", "", false, false,
            null, null, State.COMPLETED);

    private static final LearningRecordCourse courseCompletion = new LearningRecordCourse("a1", "Course 1", "Type 1", 100, LocalDateTime.now());

    @Test
    public void testGenerateModuleSummaryForNotStartedModules() {
        DisplayModuleSummary summary = factory.generateDisplayModuleSummary(List.of(module1, module1), null);
        assertEquals(0, summary.getRequiredCompletedCount());
        assertEquals(2, summary.getRequiredForCompletionCount());
        assertEquals(0, summary.getInProgressCount());
        assertEquals(NULL, summary.getStatus());
    }

    @Test
    public void testGenerateModuleSummaryForInProgressRequiredModules() {
        DisplayModuleSummary summary = factory.generateDisplayModuleSummary(List.of(module3, module3, module2), null);
        assertEquals(2, summary.getRequiredCompletedCount());
        assertEquals(3, summary.getRequiredForCompletionCount());
        assertEquals(1, summary.getInProgressCount());
        assertEquals(IN_PROGRESS, summary.getStatus());
    }

    @Test
    public void testGenerateModuleSummaryForCompletedRequiredModules() {
        DisplayModuleSummary summary = factory.generateDisplayModuleSummary(List.of(module3, module3, module3), courseCompletion);
        assertEquals(3, summary.getRequiredCompletedCount());
        assertEquals(3, summary.getRequiredForCompletionCount());
        assertEquals(0, summary.getInProgressCount());
        assertEquals(COMPLETED, summary.getStatus());
    }

    @Test
    public void testGenerateModuleSummaryForNonRequiredModules() {
        DisplayModuleSummary summary = factory.generateDisplayModuleSummary(List.of(module1, module4, module5), null);
        assertEquals(0, summary.getRequiredCompletedCount());
        assertEquals(1, summary.getRequiredForCompletionCount());
        assertEquals(2, summary.getInProgressCount());
        assertEquals(IN_PROGRESS, summary.getStatus());
    }

}
