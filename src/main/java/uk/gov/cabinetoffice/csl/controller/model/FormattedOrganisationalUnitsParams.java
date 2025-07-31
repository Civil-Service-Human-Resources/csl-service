package uk.gov.cabinetoffice.csl.controller.model;

import lombok.Data;

import java.util.List;

import static com.azure.core.util.CoreUtils.isNullOrEmpty;

@Data
public class FormattedOrganisationalUnitsParams {
    private List<Long> organisationId;
    String domain;
    boolean tierOne;

    public boolean shouldGetAll() {
        return !tierOne && isNullOrEmpty(organisationId) && domain == null;
    }

    public boolean hasOrganisationIds(Long id) {
        return !isNullOrEmpty(organisationId) && organisationId.contains(id);
    }
    
}
