package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ITypedLearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleRecord implements Cacheable {

    private Long id;
    private String uid;
    private String userId;
    private String courseId;
    /*
    Temporary whilst we're dual-running the legacy course_record table
     */
    private String courseTitle;
    private String moduleId;
    private String moduleTitle;
    private ModuleType moduleType;
    private Long duration;
    private String eventId;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate eventDate;
    private Boolean optional = Boolean.FALSE;
    private BigDecimal cost;
    private State state;
    private Result result;
    private Boolean rated = Boolean.FALSE;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    @JsonIgnore
    private boolean isNewRecord = false;

    public ModuleRecord(String courseId, String courseTitle, String userId, String moduleId, String moduleTitle, ModuleType moduleType,
                        Long duration, Boolean optional, BigDecimal cost) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.userId = userId;
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.moduleType = moduleType;
        this.duration = duration;
        this.optional = optional;
        this.cost = cost;
    }

    @JsonIgnore
    public String getCacheableId() {
        return getLearnerRecordId().getAsString();
    }

    public State getState() {
        return Objects.requireNonNullElse(this.state, State.NULL);
    }

    public boolean equalsStates(State... states) {
        return Arrays.asList(states).contains(getState());
    }

    @JsonIgnore
    public State getStateForLearningPeriod(@Nullable LearningPeriod learningPeriod) {
        State state = getState();
        if (learningPeriod != null) {
            state = State.NULL;
            LocalDateTime learningPeriodStartDateTime = learningPeriod.getStartDateAsDateTime();
            LocalDateTime completionDate = Objects.requireNonNullElse(getCompletionDate(), LocalDateTime.MIN);
            LocalDateTime updatedAt = Objects.requireNonNullElse(getUpdatedAt(), LocalDateTime.MIN);
            if (learningPeriodStartDateTime.isBefore(completionDate)) {
                state = State.COMPLETED;
            } else if (learningPeriodStartDateTime.isBefore(updatedAt)) {
                state = State.IN_PROGRESS;
            }
        }
        return state;
    }

    @JsonIgnore
    public ITypedLearnerRecordResourceID getLearnerRecordId() {
        return new ModuleRecordResourceId(userId, moduleId);
    }

    @JsonIgnore
    public String getLearnerRecordIdAsString() {
        return getLearnerRecordId().getAsString();
    }

}
