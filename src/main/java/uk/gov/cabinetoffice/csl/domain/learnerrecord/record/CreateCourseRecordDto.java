package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class CreateCourseRecordDto {

    /**
     * CourseID
     */
    private String resourceId;
    private String learnerId;
    private LocalDateTime createdTimestamp;
    private List<LearnerRecordEventDto> events;

    public CreateCourseRecordDto(String resourceId, String learnerId, List<LearnerRecordEventDto> events) {
        this.resourceId = resourceId;
        this.learnerId = learnerId;
        this.events = events;
    }
}
