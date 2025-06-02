package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyToken {
    private Long id;
    private String token;
    private String uid;
    private int capacity;
    private Set<AgencyDomain> agencyDomains;
}
