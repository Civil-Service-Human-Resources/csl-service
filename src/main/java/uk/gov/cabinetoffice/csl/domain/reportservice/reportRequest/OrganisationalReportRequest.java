package uk.gov.cabinetoffice.csl.domain.reportservice.reportRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrganisationalReportRequest extends ReportRequest {

    private List<Integer> organisationIds;

}
