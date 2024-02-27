package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.util.TestDataService;

public abstract class BaseEventModuleRecordActionTest<T extends EventModuleRecordActionProcessor> extends TestDataService {

    protected Course course = generateCourse(true, true);
    protected User user = generateUser();
    protected Module module = course.getModule(getModuleId());
    protected Event event = module.getEvent(getEventId());
    protected CourseWithModuleWithEvent courseWithModuleWithEvent = new CourseWithModuleWithEvent(course, module, event);

    protected T actionUnderTest = buildProcessor();

    protected abstract T buildProcessor();
}
