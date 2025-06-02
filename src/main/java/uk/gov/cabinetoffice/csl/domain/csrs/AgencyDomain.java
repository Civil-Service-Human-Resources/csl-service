package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyDomain {
    private Long id;
    private String domain;
}
