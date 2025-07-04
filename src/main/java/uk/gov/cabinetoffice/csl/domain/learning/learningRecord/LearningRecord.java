package uk.gov.cabinetoffice.csl.domain.learning.learningRecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LearningRecord {
    public RequiredLearningRecord requiredLearningRecord;
    public List<LearningRecordCourse> otherLearning;

    @JsonIgnore
    public void sort() {
        this.requiredLearningRecord.completedCourses.sort(Comparator.comparing(LearningRecordCourse::getCompletionDate));
        this.otherLearning.sort(Comparator.comparing(LearningRecordCourse::getCompletionDate));
    }
}
