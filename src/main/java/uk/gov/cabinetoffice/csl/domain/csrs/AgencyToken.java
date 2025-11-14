package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgencyToken implements Serializable {
    private Long id;
    private String uid;
    private String token;
    private int capacity;
    private int capacityUsed;
    private Set<AgencyDomain> agencyDomains;

    public boolean hasDomain(String domain) {
        return this.agencyDomains.stream().anyMatch(a -> a.getDomain().equals(domain));
    }

    public AgencyToken(String token, int capacity, Set<AgencyDomain> agencyDomains) {
        this.token = token;
        this.capacity = capacity;
        this.agencyDomains = agencyDomains;
    }
}
