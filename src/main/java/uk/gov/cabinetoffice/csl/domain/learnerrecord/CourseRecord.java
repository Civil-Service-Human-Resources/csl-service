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
import java.util.*;
import java.util.stream.Collectors;

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

    @JsonIgnore
    public boolean equalsAnyState(State... states) {
        return Arrays.stream(states).anyMatch(s -> getState().equals(s));
    }

    public State getState() {
        return Objects.requireNonNullElse(this.state, State.NULL);
    }

    @JsonProperty("modules")
    private Collection<ModuleRecord> moduleRecords = new ArrayList<>();

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    @JsonProperty("state")
    public State getStateJson() {
        return this.state;
    }

    @JsonIgnore
    public Map<String, ModuleRecord> getModuleRecordsAsMap() {
        return getModuleRecords().stream().collect(Collectors.toMap(ModuleRecord::getModuleId, moduleRecord -> moduleRecord));
    }
}
