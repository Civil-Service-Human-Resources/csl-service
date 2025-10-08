package uk.gov.cabinetoffice.csl.controller.csrs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteDomainDto {
    private boolean includeSubOrganisations;
}
