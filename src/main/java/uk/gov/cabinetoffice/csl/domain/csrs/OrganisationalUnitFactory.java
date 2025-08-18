package uk.gov.cabinetoffice.csl.domain.csrs;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganisationalUnitFactory {

    public OrganisationalUnitMap buildOrganisationalUnits(List<OrganisationalUnit> organisationalUnits) {
        OrganisationalUnitMap orgMap = OrganisationalUnitMap.buildFromList(organisationalUnits);
        organisationalUnits
                .forEach(o -> {
                    StringBuilder formattedName = new StringBuilder(o.getNameWithAbbreviation());
                    Long parentId = o.getParentId();
                    int parents = 0;
                    while (parentId != null) {
                        OrganisationalUnit parentOrganisationalUnit = orgMap.get(parentId);
                        if (parents == 0) {
                            parentOrganisationalUnit.addChildId(o.getId());
                            parents++;
                        }
                        formattedName.insert(0, parentOrganisationalUnit.getNameWithAbbreviation() + " | ");
                        parentId = parentOrganisationalUnit.getParentId();
                    }
                    o.setFormattedName(formattedName.toString());
                    orgMap.put(o.getId(), o);
                });
        return orgMap;
    }

}
