package uk.gov.cabinetoffice.csl.service.report.params;

import java.util.List;

public interface ISelectedOrganisationalReportRequestParams extends IReportRequestParams {
    List<Long> getSelectedOrganisationIds();
}
