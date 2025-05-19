package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.IModuleAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.UtilService;

import static org.mockito.Mockito.mock;

public abstract class BaseEventModuleRecordActionTest<T extends IModuleAction> extends TestDataService {

    protected UtilService utilService = mock(UtilService.class);
    protected Course course = generateCourse(true, true);
    protected Module module = course.getModule(getModuleId());
    protected Event event = module.getEvent(getEventId());
    protected T actionUnderTest = buildProcessor();

    protected abstract T buildProcessor();
}
