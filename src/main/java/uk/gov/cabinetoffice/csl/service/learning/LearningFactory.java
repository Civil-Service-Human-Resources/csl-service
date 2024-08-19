package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningFactory<F extends IDisplayCourseFactory> {

    private final F displayCourseFactory;

    public Learning buildDetailedLearning(List<Course> courses, List<CourseRecord> courseRecords,
                                          User user) {
        Map<String, CourseRecord> courseRecordMap = courseRecords.stream().collect(Collectors.toMap(CourseRecord::getCourseId, courseRecord -> courseRecord));
        List<DisplayCourse> displayCourses = new ArrayList<>();
        for (Course course : courses) {
            CourseRecord courseRecord = courseRecordMap.get(course.getId());
            if (courseRecord != null) {
                displayCourses.add(displayCourseFactory.generateDetailedDisplayCourse(course, user, courseRecord));
            } else {
                displayCourses.add(displayCourseFactory.generateDetailedDisplayCourse(course, user));
            }
        }
        return new Learning(displayCourses);
    }

}
