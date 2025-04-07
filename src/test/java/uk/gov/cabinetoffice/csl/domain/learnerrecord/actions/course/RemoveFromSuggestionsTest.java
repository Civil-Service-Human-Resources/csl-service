package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.IncorrectStateException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveFromSuggestionsTest extends BaseCourseRecordActionTest<RemoveFromSuggestions> {

    @Override
    protected RemoveFromSuggestions buildProcessor() {
        return new RemoveFromSuggestions(utilService, course, user);
    }

    @Test
    public void testRemoveFromSuggestionsNewRecord() {
        CourseRecord cr = actionUnderTest.generateNewCourseRecord();
        assertEquals(Preference.DISLIKED, cr.getPreference());
    }

    @Test
    public void testRemoveFromSuggestionsExistingRecord() {
        assertThrows(IncorrectStateException.class, () -> {
            actionUnderTest.applyUpdatesToCourseRecord(new CourseRecord(), null);
        });
    }
}
