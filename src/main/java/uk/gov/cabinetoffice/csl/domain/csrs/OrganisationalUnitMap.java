package uk.gov.cabinetoffice.csl.domain.csrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class OrganisationalUnitMap extends HashMap<Long, OrganisationalUnit> {

    public static OrganisationalUnitMap buildFromList(List<OrganisationalUnit> list) {
        OrganisationalUnitMap map = new OrganisationalUnitMap();
        for (OrganisationalUnit organisationalUnit : list) {
            map.put(organisationalUnit.getId(), organisationalUnit);
        }
        return map;
    }

    public List<OrganisationalUnit> getMultiple(Collection<Long> ids, boolean includeChildren) {
        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        ids.forEach(id -> {
            OrganisationalUnit organisationalUnit = get(id);
            if (organisationalUnit != null) {
                organisationalUnits.add(organisationalUnit);
                if (includeChildren) {
                    List<OrganisationalUnit> childOrgs = getMultiple(organisationalUnit.getChildIds(), includeChildren);
                    organisationalUnits.addAll(childOrgs);
                }
            }
        });
        return organisationalUnits;
    }

}
