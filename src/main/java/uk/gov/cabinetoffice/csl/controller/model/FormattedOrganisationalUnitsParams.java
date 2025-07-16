package uk.gov.cabinetoffice.csl.controller.model;

import lombok.Data;

import java.util.List;

@Data
public class FormattedOrganisationalUnitsParams {
    private List<Integer> organisationId;
    String domain;
}
