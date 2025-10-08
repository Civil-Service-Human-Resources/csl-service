package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisationalUnit implements Serializable {
    private Long id;
    private String name;
    private String code;
    private String abbreviation;
    private Long parentId;
    private OrganisationalUnit parent;
    private List<Domain> domains;
    private AgencyToken agencyToken;

    // Custom data
    private String formattedName;

    @JsonIgnore
    private AgencyToken inheritedAgencyToken;

    @JsonIgnore
    private Set<Long> childIds = new HashSet<>();

    public OrganisationalUnit(Long id, String name, String code, String abbreviation, OrganisationalUnit parent) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.abbreviation = abbreviation;
        this.parent = parent;
    }

    @JsonIgnore
    public boolean hasDomain(String domain) {
        boolean hasDomain = domainExists(domain);
        if (!hasDomain) {
            hasDomain = getAgencyTokenOrInherited().map(a -> a.hasDomain(domain)).orElse(false);
        }
        return hasDomain;
    }

    @JsonIgnore
    public Optional<AgencyToken> getAgencyTokenOrInherited() {
        return this.agencyToken == null ? Optional.ofNullable(this.inheritedAgencyToken) : Optional.of(this.agencyToken);
    }

    @JsonIgnore
    public String getNameWithAbbreviation() {
        if (isNotBlank(abbreviation)) {
            return name + " (" + abbreviation + ")";
        }
        return name;
    }

    @JsonIgnore
    public void addChildId(Long childId) {
        childIds.add(childId);
    }

    public boolean domainExists(String domain) {
        return Objects.requireNonNullElse(this.domains, new ArrayList<Domain>()).stream().anyMatch(d -> d.getDomain().equals(domain));
    }

    @JsonIgnore
    public void addDomainAndSort(Domain domain) {
        if (!domainExists(domain.domain)) {
            domains.add(domain);
            sortDomainsByName();
        }
    }

    @JsonIgnore
    private void sortDomainsByName() {
        domains.sort(Comparator.comparing(Domain::getDomain));
    }

    @JsonIgnore
    public void removeDomain(Long domainId) {
        domains.removeIf(d -> d.getId().equals(domainId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationalUnit that = (OrganisationalUnit) o;

        return new EqualsBuilder().append(id, that.id).append(name, that.name).append(code, that.code).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(name).append(code).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("parentId", parentId)
                .append("childIds", childIds)
                .append("name", name)
                .append("formattedName", formattedName)
                .append("code", code)
                .append("abbreviation", abbreviation)
                .append("domains", domains)
                .append("agencyToken", agencyToken)
                .toString();
    }

}
