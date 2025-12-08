package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.service.report.params.ISelectedOrganisationalReportRequestParams;
import uk.gov.cabinetoffice.csl.validators.frontendUrl.ValidFrontendUrl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestWithSelectedOrganisationIdsParams extends SelectedOrganisationIdsCourseCompletionsParams implements ISelectedOrganisationalReportRequestParams {

    @NotNull
    protected String userId;

    @NotNull
    protected String userEmail;

    @NotNull
    @ValidFrontendUrl
    protected String downloadBaseUrl;

    @NotNull
    protected String fullName;

}
