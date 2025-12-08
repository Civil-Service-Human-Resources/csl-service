package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.reportservice.reportRequest.ReportRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetReportRequestsResponse<T extends ReportRequest> {
    private List<T> requests;

    public boolean hasRequests() {
        return !this.getRequests().isEmpty();
    }
}
