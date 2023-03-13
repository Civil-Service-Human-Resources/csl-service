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
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseRecord {

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

    public ModuleRecord getModuleRecord(String moduleId) {
        if(moduleRecords != null) {
            return this.moduleRecords.stream()
                    .filter(moduleRecord -> moduleId.equals(moduleRecord.getModuleId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public Collection<ModuleRecord> updateModuleRecords(ModuleRecord newModuleRecord) {
        return moduleRecords.stream().map(existingModuleRecord ->
                        existingModuleRecord.getModuleId().equals(newModuleRecord.getModuleId())
                                ? newModuleRecord : existingModuleRecord)
                .collect(toList());
    }
}
