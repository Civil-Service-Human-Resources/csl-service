package uk.gov.cabinetoffice.csl.service.report.params;

import jakarta.validation.constraints.Size;

import java.util.List;

public interface ISelectedOrganisationalReportRequestParams extends IReportRequestParams {
    @Size(min = 1)
    List<Long> getSelectedOrganisationIds();
}
