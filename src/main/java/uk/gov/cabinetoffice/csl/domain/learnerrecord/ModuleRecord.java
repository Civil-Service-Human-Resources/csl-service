package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModuleRecord implements Serializable {

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
}
