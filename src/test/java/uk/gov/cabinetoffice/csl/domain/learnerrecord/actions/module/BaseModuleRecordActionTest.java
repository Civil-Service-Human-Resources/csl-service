package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.UtilService;

import static org.mockito.Mockito.mock;

public abstract class BaseModuleRecordActionTest<T extends ModuleRecordActionProcessor> extends TestDataService {

    protected UtilService utilService = mock(UtilService.class);
    protected Course course = generateCourse(true, false);
    protected User user = generateUser();
    protected Module module = course.getModule(getModuleId());
    protected CourseWithModule courseWithModule = new CourseWithModule(course, module);

    protected T actionUnderTest = buildProcessor();

    protected abstract T buildProcessor();
}
