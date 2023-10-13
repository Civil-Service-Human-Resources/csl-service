package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRecord implements Serializable {

    private String courseId;

    private String userId;

    private String courseTitle;

    private State state;

    private String preference;

    @JsonIgnore
    private String profession;

    @JsonIgnore
    private String department;

    @JsonIgnore
    private boolean isRequired;

    private Collection<ModuleRecord> moduleRecords;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return moduleRecords;
    }

    @JsonIgnore
    public State getStateSafe() {
        return Objects.requireNonNullElse(this.state, State.NULL);
    }

    public ModuleRecord getModuleRecord(String moduleId) {
        if (moduleRecords != null) {
            return this.moduleRecords.stream()
                    .filter(moduleRecord -> moduleId.equals(moduleRecord.getModuleId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void updateModuleRecords(ModuleRecord newModuleRecord) {
        Map<String, ModuleRecord> records = moduleRecords.stream().
                collect(Collectors.toMap(ModuleRecord::getModuleId, Function.identity()));
        records.put(newModuleRecord.getModuleId(), newModuleRecord);
        ArrayList<ModuleRecord> updatedRecords = new ArrayList<>(records.values());
        this.setModuleRecords(updatedRecords);
    }
}
