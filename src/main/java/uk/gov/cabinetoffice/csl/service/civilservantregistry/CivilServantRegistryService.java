package uk.gov.cabinetoffice.csl.service.civilservantregistry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.civilservantregistry.ICivilServantRegistryClient;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitName;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CivilServantRegistryService {

    private final ICivilServantRegistryClient civilServantRegistryClient;

    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return civilServantRegistryClient.getAllOrganisationalUnits();
    }

    public List<FormattedOrganisationalUnitName> getFormattedOrganisationalUnitNames() {
        log.info("Getting formatted organisational unit names");
        List<OrganisationalUnit> allOrganisationalUnits = getAllOrganisationalUnits();
        Map<Long, OrganisationalUnit> orgMap = allOrganisationalUnits.stream()
                .collect(Collectors.toMap(OrganisationalUnit::getId, o -> o));
        return allOrganisationalUnits
            .stream()
            .map(o -> {
                StringBuilder formattedName = new StringBuilder(o.getName());
                Long parentId = o.getParentId();
                while(parentId != null) {
                    OrganisationalUnit parentOrganisationalUnit = orgMap.get(parentId);
                    String parentName = parentOrganisationalUnit.getName();
                    formattedName.insert(0, parentName + " | ");
                    parentId = parentOrganisationalUnit.getParentId();
                }
                FormattedOrganisationalUnitName fn = new FormattedOrganisationalUnitName();
                fn.setId(o.getId());
                fn.setFormattedName(formattedName.toString());
                return fn;
            }).toList();
    }
}
