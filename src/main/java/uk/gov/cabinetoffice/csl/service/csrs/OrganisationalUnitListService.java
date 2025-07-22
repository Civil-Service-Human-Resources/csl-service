package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnits;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
@AllArgsConstructor
public class OrganisationalUnitListService {
    private final ICSRSClient civilServantRegistryClient;

    @Cacheable("organisations")
    public OrganisationalUnits getAllOrganisationalUnitsWithChildren() {
        log.info("Getting all organisational units");
        return new OrganisationalUnits(setFormattedName(civilServantRegistryClient.getAllOrganisationalUnits(true)));
    }

    @CacheEvict(value = "organisations", allEntries = true)
    public void removeOrganisationsFromCache() {
        log.info("Organisations are removed from the cache.");
    }

    public List<OrganisationalUnit> setFormattedName(List<OrganisationalUnit> allOrganisationalUnits) {
        Map<Long, OrganisationalUnit> orgMap = allOrganisationalUnits.stream()
                .collect(toMap(OrganisationalUnit::getId, o -> o));

        return allOrganisationalUnits.stream()
                .peek(o -> {
                    StringBuilder formattedName = new StringBuilder(getFormattedNameWithAbbreviation(o.getName(), o.getAbbreviation()));
                    Long parentId = o.getParentId();
                    while (parentId != null) {
                        OrganisationalUnit parentOrganisationalUnit = orgMap.get(parentId);
                        String parentName = getFormattedNameWithAbbreviation(parentOrganisationalUnit.getName(), parentOrganisationalUnit.getAbbreviation());
                        formattedName.insert(0, parentName + " | ");
                        parentId = parentOrganisationalUnit.getParentId();
                    }
                    o.setFormattedName(formattedName.toString());
                }).toList();
    }

    private String getFormattedNameWithAbbreviation(String name, String abbreviation) {
        if (isNotBlank(abbreviation)) {
            return name + " (" + abbreviation + ")";
        }
        return name;
    }
}
