package uk.gov.cabinetoffice.csl.domain.csrs;

import java.util.*;

public class OrganisationalUnitMap extends HashMap<Long, OrganisationalUnit> {

    public static OrganisationalUnitMap buildFromList(List<OrganisationalUnit> list) {
        OrganisationalUnitMap map = new OrganisationalUnitMap();
        for (OrganisationalUnit organisationalUnit : list) {
            map.put(organisationalUnit.getId(), organisationalUnit);
        }
        return map;
    }

    public void remove(Collection<Long> ids) {
        ids.forEach(this::remove);
    }

    public List<OrganisationalUnit> getHierarchy(Long organisationId) {
        OrganisationalUnit organisationalUnit = get(organisationId);
        List<OrganisationalUnit> hierarchy = new ArrayList<>(List.of(organisationalUnit));
        Long parentId = organisationalUnit.getParentId();
        while (parentId != null) {
            OrganisationalUnit parent = get(parentId);
            hierarchy.add(parent);
            parentId = parent.getParentId();
        }
        return hierarchy;
    }

    public Map<Long, List<OrganisationalUnit>> getHierarchies(List<Long> organisationIds) {
        Map<Long, List<OrganisationalUnit>> hierarchies = new HashMap<>();
        organisationIds.forEach(organisationId -> hierarchies.put(organisationId, getHierarchy(organisationId)));
        return hierarchies;
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
