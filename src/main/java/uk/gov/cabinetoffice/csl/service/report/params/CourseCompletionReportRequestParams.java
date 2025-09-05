package uk.gov.cabinetoffice.csl.service.report.params;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationIdsCourseCompletionsParams;
import uk.gov.cabinetoffice.csl.validators.frontendUrl.ValidFrontendUrl;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class CourseCompletionReportRequestParams extends OrganisationIdsCourseCompletionsParams implements IOrganisationalReportRequestParams {
    @NotNull
    protected String userId;

    @NotNull
    protected String userEmail;

    @NotNull
    @ValidFrontendUrl
    protected String downloadBaseUrl;

    protected String fullName;
}
