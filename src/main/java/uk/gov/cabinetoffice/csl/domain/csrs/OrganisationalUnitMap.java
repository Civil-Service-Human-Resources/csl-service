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

    public List<OrganisationalUnit> updateFormattedName(List<OrganisationalUnit> organisationalUnits) {
        organisationalUnits
            .forEach(o -> {
                StringBuilder formattedName = new StringBuilder(o.getNameWithAbbreviation());
                Long parentId = o.getParentId();
                int parents = 0;
                while (parentId != null) {
                    OrganisationalUnit parentOrganisationalUnit = this.get(parentId);
                    if (parents == 0) {
                        parentOrganisationalUnit.addChildId(o.getId());
                        parents++;
                    }
                    formattedName.insert(0, parentOrganisationalUnit.getNameWithAbbreviation() + " | ");
                    parentId = parentOrganisationalUnit.getParentId();
                }
                o.setFormattedName(formattedName.toString());
                this.put(o.getId(), o);
            });
        return organisationalUnits;
    }
}
