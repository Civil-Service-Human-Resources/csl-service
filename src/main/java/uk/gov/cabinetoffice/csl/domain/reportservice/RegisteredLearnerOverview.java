package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisteredLearnerOverview {
    private final boolean hasRequests;
}
