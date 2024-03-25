package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.error.RecordNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
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

    private Preference preference;

    @JsonIgnore
    private boolean isRequired;

    private Collection<ModuleRecord> moduleRecords = new ArrayList<>();

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdated;

    @JsonProperty("modules")
    public Collection<ModuleRecord> getModuleRecords() {
        return moduleRecords;
    }

    @JsonProperty("state")
    public State getStateJson() {
        return this.state;
    }

    public State getState() {
        return Objects.requireNonNullElse(this.state, State.NULL);
    }

    public CourseRecord(String courseId, String userId, String courseTitle) {
        this.courseId = courseId;
        this.userId = userId;
        this.courseTitle = courseTitle;
    }

    @JsonIgnore
    public ModuleRecord getOrCreateModuleRecord(Module module) {
        return getModuleRecord(module.getId()).orElseGet(() -> createModuleRecord(module));
    }

    @JsonIgnore
    public Optional<ModuleRecord> getModuleRecord(String moduleId) {
        return moduleRecords.stream()
                .filter(mr -> mr.getModuleId().equals(moduleId))
                .findFirst();
    }

    @JsonIgnore
    public ModuleRecord createModuleRecord(Module module) {
        ModuleRecord moduleRecord = new ModuleRecord(module.getId(), module.getTitle(), module.getModuleType(),
                module.getDuration(), module.isOptional(), module.getCost());
        this.moduleRecords.add(moduleRecord);
        return moduleRecord;
    }

    @JsonIgnore
    public ModuleRecord getModuleRecordAndThrowIfNotFound(String moduleId) {
        return getModuleRecord(moduleId).orElseThrow(() -> new RecordNotFoundException(String.format("Module '%s' in course '%s'", moduleId, courseId)));
    }

    @JsonIgnore
    public void updateModuleRecord(ModuleRecord moduleRecord) {
        updateModuleRecords(List.of(moduleRecord));
    }

    @JsonIgnore
    public void updateModuleRecords(Collection<ModuleRecord> recordUpdates) {
        Map<String, ModuleRecord> records = moduleRecords.stream().
                collect(Collectors.toMap(ModuleRecord::getModuleId, Function.identity()));
        recordUpdates.forEach(mr -> {
            ModuleRecord moduleRecord = records.get(mr.getModuleId());
            if (moduleRecord == null) {
                records.put(mr.getModuleId(), mr);
            } else {
                records.replace(mr.getModuleId(), mr);
            }
        });
        this.setModuleRecords(new ArrayList<>(records.values()));
    }

    @JsonIgnore
    public void update(CourseRecord input) {
        this.state = input.getState();
        this.preference = input.getPreference();
        this.lastUpdated = input.getLastUpdated();
        this.updateModuleRecords(input.getModuleRecords());
    }
}
