package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @deprecated We should move away from using course records as it is a legacy data item.
 * <p>
 * Use The ILearnerRecord.java interface for calculating course state
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRecord {

    private String courseId;

    private String userId;

    private String courseTitle;

    private State state;

    private Preference preference;

    private Collection<ModuleRecord> moduleRecords = new ArrayList<>();

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    public LearnerRecordResourceId getLearnerRecordId() {
        return new LearnerRecordResourceId(userId, courseId);
    }

    @JsonProperty("state")
    public State getStateJson() {
        return this.state;
    }

    @JsonIgnore
    public Optional<ModuleRecord> getModuleRecord(String moduleId) {
        return moduleRecords.stream()
                .filter(mr -> mr.getModuleId().equals(moduleId))
                .findFirst();
    }

    public State getState() {
        return Objects.requireNonNullElse(this.state, State.NULL);
    }

    public CourseRecord(String courseId, String userId, String courseTitle) {
        this.courseId = courseId;
        this.userId = userId;
        this.courseTitle = courseTitle;
    }

}
