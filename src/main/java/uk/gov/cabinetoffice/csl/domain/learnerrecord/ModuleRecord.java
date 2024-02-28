package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleRecord implements Serializable {

    private Long id;
    private String uid;
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

    public static ModuleRecord fromModule(Module module) {
        return new ModuleRecord(module.getId(), module.getTitle(), module.getModuleType(),
                module.getDuration(), module.isOptional(), module.getCost());
    }

    public ModuleRecord(String moduleId, String moduleTitle, ModuleType moduleType,
                        Long duration, Boolean optional, BigDecimal cost) {
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.moduleType = moduleType;
        this.duration = duration;
        this.optional = optional;
        this.cost = cost;
    }

    public State getState() {
        return Objects.requireNonNullElse(this.state, State.NULL);
    }


}
