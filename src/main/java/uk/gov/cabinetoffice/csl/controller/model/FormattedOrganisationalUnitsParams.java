package uk.gov.cabinetoffice.csl.controller.model;

import lombok.Data;

@Data
public class FormattedOrganisationalUnitsParams {
    private Integer[] organisationId;
    String domain;
}
