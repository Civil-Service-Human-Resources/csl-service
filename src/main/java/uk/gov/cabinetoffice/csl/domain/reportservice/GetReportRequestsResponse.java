package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetReportRequestsResponse {
    private List<CourseCompletionReportRequest> requests;

    public boolean hasRequests() {
        return !this.getRequests().isEmpty();
    }
}
