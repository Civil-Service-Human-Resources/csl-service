package uk.gov.cabinetoffice.csl.controller.csrs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetOrganisationUnitsFilter {

    private List<Long> organisationId;
    private boolean includeParents = false;

}
