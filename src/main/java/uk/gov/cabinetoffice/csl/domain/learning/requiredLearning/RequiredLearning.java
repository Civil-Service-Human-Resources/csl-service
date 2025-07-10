package uk.gov.cabinetoffice.csl.domain.learning.requiredLearning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequiredLearning {
    private String userId;
    private List<RequiredLearningCourse> courses;

    public void sortCourses() {
        this.courses.sort(Comparator.comparing(RequiredLearningCourse::getDueBy).reversed());
    }
}
