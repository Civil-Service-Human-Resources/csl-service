package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.service.report.params.ISelectedOrganisationalReportRequestParams;
import uk.gov.cabinetoffice.csl.validators.frontendUrl.ValidFrontendUrl;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredLearnerReportRequestParams implements ISelectedOrganisationalReportRequestParams {
    @Size(min = 1)
    protected List<Long> selectedOrganisationIds;

    @NotNull
    protected String userId;

    @NotNull
    protected String userEmail;

    @NotNull
    @ValidFrontendUrl
    protected String downloadBaseUrl;

    @NotNull
    protected String fullName;

    @NotNull
    protected ZoneId timezone;

    public String getTimezone() {
        return timezone.toString();
    }

    public List<Long> getSelectedOrganisationIds() {
        return this.selectedOrganisationIds == null ? new ArrayList<>() : this.selectedOrganisationIds;
    }
}
