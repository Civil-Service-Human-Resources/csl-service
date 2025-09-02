package uk.gov.cabinetoffice.csl.domain.learning.learningRecord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RequiredLearningRecord {
    public List<LearningRecordCourse> completedCourses;
    public Integer totalRequired;
}
