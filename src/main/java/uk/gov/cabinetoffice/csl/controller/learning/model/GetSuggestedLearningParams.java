package uk.gov.cabinetoffice.csl.controller.learning.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSuggestedLearningParams {

    @Max(10)
    @Min(1)
    private Integer size = 6;

}
