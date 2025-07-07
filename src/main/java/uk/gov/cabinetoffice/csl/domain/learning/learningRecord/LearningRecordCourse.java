package uk.gov.cabinetoffice.csl.domain.learning.learningRecord;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LearningRecordCourse {
    public String id;
    public String title;
    public String type;
    public Integer duration;
    public LocalDateTime completionDate;
}
