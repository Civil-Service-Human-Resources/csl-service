package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetRequiredLearningForDepartmentsParams {
    List<Long> organisationIds;
    @JsonSerialize(using = LocalDateSerializer.class)
    LocalDate dueBy;
}
