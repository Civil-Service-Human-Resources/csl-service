package uk.gov.cabinetoffice.csl.service.learning;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayCourse;
import uk.gov.cabinetoffice.csl.domain.learning.Learning;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecord;
import uk.gov.cabinetoffice.csl.domain.learning.learningRecord.LearningRecordCourse;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningFactory {

    private final LearningRecordService learningRecordService;
    private final IDisplayCourseFactory displayCourseFactory;

    public Learning buildDetailedLearning(List<Course> courses, Map<String, CourseRecord> courseRecords,
                                          User user) {

        LearningRecord learningRecord = learningRecordService.getLearningRecord(user.getId());
        List<LearningRecordCourse> userCompletedCourses = new ArrayList<>();
        if (learningRecord.getRequiredLearningRecord() != null && learningRecord.getRequiredLearningRecord().getCompletedCourses() != null) {
            userCompletedCourses.addAll(learningRecord.getRequiredLearningRecord().getCompletedCourses());
        }
        if (learningRecord.getOtherLearning() != null) {
            userCompletedCourses.addAll(learningRecord.getOtherLearning());
        }

        List<DisplayCourse> displayCourses = courses.stream().map(c -> {
            CourseRecord courseRecord = courseRecords.get(c.getCacheableId());

            LearningRecordCourse userLearningRecordCourse = null;
            Optional<LearningRecordCourse> completedCourse = userCompletedCourses
                    .stream()
                    .filter(course -> course.getId().equals(c.getId()))
                    .findFirst();

            if (completedCourse.isPresent()) {
                userLearningRecordCourse = completedCourse.get();
            }


            return displayCourseFactory.generateDetailedDisplayCourse(c, user, courseRecord, userLearningRecordCourse);
        }).toList();
        return new Learning(displayCourses);
    }

}
