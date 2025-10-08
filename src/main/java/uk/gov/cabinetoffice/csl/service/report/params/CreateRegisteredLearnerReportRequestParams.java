package uk.gov.cabinetoffice.csl.service.report.params;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZoneId;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRegisteredLearnerReportRequestParams implements IOrganisationalReportRequestParams {
    protected List<Long> organisationIds;
    protected String userId;
    protected String userEmail;
    protected String downloadBaseUrl;
    protected String fullName;
    protected ZoneId timezone;

    public String getTimezone() {
        return timezone.toString();
    }
}
