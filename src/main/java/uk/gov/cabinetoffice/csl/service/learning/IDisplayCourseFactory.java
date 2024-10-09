package uk.gov.cabinetoffice.csl.service.learning;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

public interface IDisplayCourseFactory {

    DisplayCourse generateDetailedDisplayCourse(Course course, User user, CourseRecord courseRecord);

    DisplayCourse generateDetailedDisplayCourse(Course course, User user);

}
