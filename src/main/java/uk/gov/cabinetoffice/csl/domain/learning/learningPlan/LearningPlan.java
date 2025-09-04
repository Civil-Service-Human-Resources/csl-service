package uk.gov.cabinetoffice.csl.domain.learning.learningPlan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LearningPlan {
    private String userId;
    private List<BookedLearningPlanCourse> bookedCourses;
    private List<LearningPlanCourse> learningPlanCourses;

    public void sortCourses() {
        this.bookedCourses.sort(Comparator.comparing(BookedLearningPlanCourse::getEventModule, Comparator.comparing(EventModule::getBookedDate)).reversed());
    }
}
