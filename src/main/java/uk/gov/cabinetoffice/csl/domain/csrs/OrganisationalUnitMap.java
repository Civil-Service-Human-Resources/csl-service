package uk.gov.cabinetoffice.csl.domain.csrs;

import org.springframework.lang.Nullable;

import java.util.*;

import static java.util.Collections.singletonList;

public class OrganisationalUnitMap extends HashMap<Long, OrganisationalUnit> {

    public static OrganisationalUnitMap create(List<OrganisationalUnit> organisationalUnits) {
        OrganisationalUnitMap map = new OrganisationalUnitMap();
        for (OrganisationalUnit organisationalUnit : organisationalUnits) {
            map.put(organisationalUnit.getId(), organisationalUnit);
        }
        organisationalUnits.forEach(map::buildOrganisationalUnit);
        return map;
    }

    public OrganisationalUnit get(Long id) {
        return Optional.ofNullable(super.get(id)).orElseThrow(() -> new IllegalArgumentException("Organisational unit not found for id: " + id));
    }

    public AgencyToken getAgencyToken(Long id) {
        return Optional.ofNullable(get(id).getAgencyToken()).orElseThrow(() -> new IllegalArgumentException("Agency token not found for organisation id: " + id));
    }

    private BasicOrganisationalUnitNode buildNode(OrganisationalUnit organisationalUnit) {
        List<BasicOrganisationalUnitNode> childNodes = organisationalUnit.getChildIds().stream().map(id -> buildNode(get(id))).toList();
        return new BasicOrganisationalUnitNode(organisationalUnit.getName(), organisationalUnit.getId(), childNodes);
    }

    public List<BasicOrganisationalUnitNode> getOrganisationalUnitTree() {
        return values()
                .stream().filter(o -> o.getParentId() == null)
                .map(this::buildNode).toList();
    }

    public List<Long> delete(Collection<Long> ids) {
        return ids.stream().map(this::delete).flatMap(Collection::stream).toList();
    }

    public List<Long> delete(Long id) {
        List<Long> idsToRemove = getMultiple(Collections.singleton(id), true)
                .stream().map(OrganisationalUnit::getId).toList();
        idsToRemove.forEach(this::remove);
        return idsToRemove;
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
                    List<OrganisationalUnit> childOrgs = getMultiple(organisationalUnit.getChildIds(), true);
                    organisationalUnits.addAll(childOrgs);
                }
            }
        });
        return organisationalUnits;
    }

    public void updateAgencyToken(Long organisationalUnitId, @Nullable AgencyToken newAgencyToken) {
        updateAgencyToken(organisationalUnitId, newAgencyToken, false);
    }

    private void updateAgencyToken(Long organisationalUnitId, @Nullable AgencyToken newAgencyToken, boolean inherited) {
        OrganisationalUnit organisationalUnit = get(organisationalUnitId);
        if (inherited) {
            if (organisationalUnit.getAgencyToken() == null) {
                organisationalUnit.setInheritedAgencyToken(newAgencyToken);
                organisationalUnit.getChildIds().forEach(childId -> updateAgencyToken(childId, newAgencyToken, true));
            }
        } else {
            organisationalUnit.setAgencyToken(newAgencyToken);
            organisationalUnit.getChildIds().forEach(childId -> updateAgencyToken(childId, newAgencyToken, true));
        }
    }

    public OrganisationalUnit setOrganisationalUnitData(OrganisationalUnit organisationalUnit) {
        StringBuilder formattedName = new StringBuilder(organisationalUnit.getNameWithAbbreviation());
        Long parentId = organisationalUnit.getParentId();
        int parents = 0;
        while (parentId != null) {
            OrganisationalUnit parentOrganisationalUnit = this.get(parentId);
            if (parents == 0) {
                organisationalUnit.setParentName(parentOrganisationalUnit.getName());
                parentOrganisationalUnit.addChildId(organisationalUnit.getId());
                parents++;
            }
            if (parentOrganisationalUnit.getAgencyToken() != null && organisationalUnit.getAgencyTokenOrInherited().isEmpty()) {
                organisationalUnit.setInheritedAgencyToken(parentOrganisationalUnit.getAgencyToken());
            }
            formattedName.insert(0, parentOrganisationalUnit.getNameWithAbbreviation() + " | ");
            parentId = parentOrganisationalUnit.getParentId();
        }
        organisationalUnit.setFormattedName(formattedName.toString());
        return organisationalUnit;
    }

    public List<OrganisationalUnit> buildOrganisationalUnit(Long organisationalUnitId, boolean includeChildren) {
        List<OrganisationalUnit> multipleOrgs = getMultiple(singletonList(organisationalUnitId), includeChildren);
        multipleOrgs = buildOrganisationalUnit(multipleOrgs);
        return multipleOrgs;
    }

    public OrganisationalUnit buildOrganisationalUnit(OrganisationalUnit organisationalUnit) {
        organisationalUnit = setOrganisationalUnitData(organisationalUnit);
        this.put(organisationalUnit.getId(), organisationalUnit);
        return organisationalUnit;
    }

    public List<OrganisationalUnit> buildOrganisationalUnit(List<OrganisationalUnit> organisationalUnits) {
        return organisationalUnits.stream().map(this::buildOrganisationalUnit).toList();
    }
}
