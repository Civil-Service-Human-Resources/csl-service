package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AddReportRequestResponse {
    private final Boolean addedSuccessfully;
    private String details;
}
