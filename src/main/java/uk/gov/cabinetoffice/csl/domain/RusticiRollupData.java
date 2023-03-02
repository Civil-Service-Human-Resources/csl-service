package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RusticiRollupData {

    private String id;

    private String  registrationCompletion;

    private String registrationSuccess;

    private Course course;

    private Learner learner;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updated;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completedDate;
}
