package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.gov.cabinetoffice.csl.validators.frontendUrl.ValidFrontendUrl;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class CreateReportRequestWithOrganisationIdsParams extends OrganisationIdsCourseCompletionsParams {
    @NotNull
    protected String userId;

    @NotNull
    protected String userEmail;

    @NotNull
    @ValidFrontendUrl
    protected String downloadBaseUrl;

    protected String fullName;
}
