package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
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
        if (this.domains == null) {
            return false;
        }

        return this.domains.stream().map(d -> d.domain)
                .toList()
                .contains(domain);
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

}
