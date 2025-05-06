package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningFactory<F extends IDisplayCourseFactory> {

    private final F displayCourseFactory;

    public Learning buildDetailedLearning(List<Course> courses, Map<String, CourseRecord> courseRecords,
                                          User user) {
        List<DisplayCourse> displayCourses = courses.stream().map(c -> {
            CourseRecord courseRecord = courseRecords.get(c.getId());
            return displayCourseFactory.generateDetailedDisplayCourse(c, user, courseRecord);
        }).toList();
        return new Learning(displayCourses);
    }

}
