package uk.gov.cabinetoffice.csl.service.report.params;

import jakarta.validation.constraints.NotNull;
import uk.gov.cabinetoffice.csl.validators.frontendUrl.ValidFrontendUrl;

public interface IReportRequestParams {

    @NotNull
    String getUserId();

    @NotNull
    String getUserEmail();

    @NotNull
    @ValidFrontendUrl
    String getDownloadBaseUrl();

    @NotNull
    String getFullName();

    void setUserId(String value);

    void setUserEmail(String value);

    void setDownloadBaseUrl(String value);

    void setFullName(String value);
}
