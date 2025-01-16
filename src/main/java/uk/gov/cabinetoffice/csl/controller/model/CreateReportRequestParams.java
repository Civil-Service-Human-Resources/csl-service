package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.validators.frontendUrl.ValidFrontendUrl;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestParams extends GetCourseCompletionsParams {

    @NotNull
    private String userId;

    @NotNull
    private String userEmail;

    @NotNull
    @ValidFrontendUrl
    private String downloadBaseUrl;

    private String fullName;

}
