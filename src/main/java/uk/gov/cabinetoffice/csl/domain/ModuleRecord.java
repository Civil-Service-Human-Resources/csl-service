package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

@Getter
@Setter
@NoArgsConstructor
public class ModuleRecord {

    private Long id;

    private String uid;

    private String moduleId;

    private String moduleTitle;

    private String moduleType;

    private Long duration;

    private String eventId;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate eventDate;

    private Boolean optional = Boolean.FALSE;

    private BigDecimal cost;

    private State state;

    private Result result;

    private String score;

    private Boolean rated = Boolean.FALSE;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completionDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    private String paymentMethod;

    private String paymentDetails;

    private BookingStatus bookingStatus;

    @JsonIgnore
    private CourseRecord courseRecord;

    public ModuleRecord(String moduleId) {
        checkArgument(moduleId != null);
        this.moduleId = moduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ModuleRecord that = (ModuleRecord) o;

        return new EqualsBuilder()
                .append(moduleId, that.moduleId)
                .append(eventId, that.eventId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(moduleId)
                .append(eventId)
                .toHashCode();
    }
}