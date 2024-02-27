package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;

public abstract class BaseCourseRecordActionTest<T extends CourseRecordActionProcessor> extends TestDataService {

    protected Course course = generateCourse(false, false);
    protected User user = generateUser();

    protected T actionUnderTest = buildProcessor();

    protected abstract T buildProcessor();
}
