package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.unmodifiableCollection;
import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseRecord {

    @JsonIgnore
    private CourseRecordIdentity identity;

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

    public CourseRecord(String courseId, String userId) {
        checkArgument(courseId != null);
        checkArgument(userId != null);
        this.identity = new CourseRecordIdentity(courseId, userId);
        this.moduleRecords = new HashSet<>();
    }

    @JsonProperty("courseId")
    public String getCourseId() {
        return identity.getCourseId();
    }

    @JsonProperty("userId")
    public String getUserId() {
        return identity.getUserId();
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CourseRecord that = (CourseRecord) o;

        return new EqualsBuilder()
                .append(identity, that.identity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(identity)
                .toHashCode();
    }
}
