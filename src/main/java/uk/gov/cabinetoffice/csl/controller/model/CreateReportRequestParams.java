package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestParams extends GetCourseCompletionsParams {

    @NotNull
    private String userId;

    @NotNull
    private String userEmail;

    @NotNull
    @URL(protocol = "https", regexp = "^https:\\/\\/(?:\\w+\\.)?learn\\.civilservice\\.gov\\.uk(?:\\/\\w+)+$")
    private String downloadBaseUrl;

    private String fullName;

}
