package uk.gov.cabinetoffice.csl.service.report.params;

import java.util.List;

public interface IOrganisationalReportRequestParams extends IReportRequestParams {
    void setOrganisationIds(List<Long> list);
}
