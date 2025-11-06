package uk.gov.cabinetoffice.csl.controller.csrs.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgencyTokenDTO {

    private String uid;
    @NotEmpty
    private Set<String> domain;
    @Min(1)
    private int capacity;
    private int capacityUsed;
    @NotEmpty
    private String token;

}
