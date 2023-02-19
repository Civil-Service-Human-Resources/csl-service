package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
        return unmodifiableCollection(moduleRecords);
    }

    public ModuleRecord getModuleRecord(String moduleId) {
        return this.moduleRecords.stream()
                .filter(moduleRecord -> moduleId.equals(moduleRecord.getModuleId()))
                .findFirst()
                .orElse(null);
    }

    public LocalDateTime getCompletionDate() {
        LocalDateTime mostRecentCompletionDate = null;
        for (ModuleRecord moduleRecord : moduleRecords) {
            if (mostRecentCompletionDate == null ||
                    moduleRecord.getCompletionDate() != null
                            && mostRecentCompletionDate.isBefore(moduleRecord.getCompletionDate())) {
                mostRecentCompletionDate = moduleRecord.getCompletionDate();
            }
        }
        return mostRecentCompletionDate;
    }
}
