package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.UtilService;

import static org.mockito.Mockito.mock;

public abstract class BaseCourseRecordActionTest<T extends CourseRecordActionProcessor> extends TestDataService {

    protected UtilService utilService = mock(UtilService.class);
    protected Course course = generateCourse(false, false);
    protected User user = generateUser();

    protected T actionUnderTest = buildProcessor();

    protected abstract T buildProcessor();
}
