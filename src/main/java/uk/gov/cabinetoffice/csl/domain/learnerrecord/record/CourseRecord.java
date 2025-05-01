package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseRecord extends LearnerRecord {
    private final Integer recordType = 1;
    private String resourceId;
    private String learnerId;
    private final Long parentId = null;
    private LocalDateTime createdTimestamp;
    private List<LearnerRecordEvent> events = new ArrayList<>();

}
