package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportRequestParams extends GetCourseCompletionsParams {

    @NotNull
    private String userId;

    @NotNull
    private String userEmail;

}
