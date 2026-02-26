package uk.gov.cabinetoffice.csl.controller.learnerRecord.model;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Period;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSkillsLearnerRecordsParams {

    @Min(1)
    @Max(200)
    private Integer size = 200;

    @NotNull
    private SkillsSyncMode mode;

    private Period frequency;

}
