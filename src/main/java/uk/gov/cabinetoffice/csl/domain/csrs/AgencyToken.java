package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgencyToken implements Serializable, Cacheable {
    private Long id;
    private String token;
    private String uid;
    private int capacity;
    private Set<AgencyDomain> agencyDomains;

    public boolean hasDomain(String domain) {
        return this.agencyDomains.stream().anyMatch(a -> a.getDomain().equals(domain));
    }

    @Override
    @JsonIgnore
    public String getCacheableId() {
        return Long.toString(id);
    }
}
