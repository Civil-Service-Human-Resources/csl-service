package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.Function;

@Slf4j
public class OrganisationalUnitMap extends HashMap<Long, OrganisationalUnit> {

    public static OrganisationalUnitMap buildFromList(List<OrganisationalUnit> organisationalUnits) {
        OrganisationalUnitMap map = new OrganisationalUnitMap();
        for (OrganisationalUnit organisationalUnit : organisationalUnits) {
            map.put(organisationalUnit.getId(), organisationalUnit);
        }
        organisationalUnits.forEach(map::setOrganisationalUnitData);
        return map;
    }

    public OrganisationalUnit get(Long id) {
        return Optional.ofNullable(super.get(id)).orElseThrow(() -> new IllegalArgumentException("Organisational unit not found for id: " + id));
    }

    private BasicOrganisationalUnitNode buildNode(OrganisationalUnit organisationalUnit) {
        List<BasicOrganisationalUnitNode> childNodes = organisationalUnit.getChildIds().stream().map(id -> buildNode(get(id)))
                .sorted(Comparator.comparing(BasicOrganisationalUnitNode::getName, String::compareToIgnoreCase)).toList();
        return new BasicOrganisationalUnitNode(organisationalUnit.getName(), organisationalUnit.getId(), childNodes);
    }

    public List<BasicOrganisationalUnitNode> getOrganisationalUnitTree() {
        return values()
                .stream().filter(o -> o.getParentId() == null)
                .map(this::buildNode)
                .sorted(Comparator.comparing(BasicOrganisationalUnitNode::getName, String::compareToIgnoreCase))
                .toList();
    }

    public OrganisationalUnit updateOrganisationalUnit(Long id, Function<OrganisationalUnit, OrganisationalUnit> update) {
        OrganisationalUnit organisationalUnit = get(id);
        if (organisationalUnit != null) {
            organisationalUnit = update.apply(organisationalUnit);
            put(organisationalUnit.getId(), organisationalUnit);
        }
        return organisationalUnit;
    }

    public List<Long> delete(Collection<Long> ids) {
        return ids.stream().map(this::delete).flatMap(Collection::stream).toList();
    }

    public List<Long> delete(Long id) {
        OrganisationalUnit organisationalUnit = get(id);
        if (organisationalUnit.getParentId() != null) {
            updateOrganisationalUnit(organisationalUnit.getParentId(), o -> {
                o.getChildIds().remove(id);
                return o;
            });
        }
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

    public List<Long> getMultipleAsIds(Collection<Long> ids, boolean includeChildren) {
        return getMultiple(ids, includeChildren).stream().map(OrganisationalUnit::getId).toList();
    }

    public List<OrganisationalUnit> rebuildHierarchy(OrganisationalUnit root) {
        ArrayList<OrganisationalUnit> hierarchy = new ArrayList<>();
        hierarchy.add(root);
        hierarchy.addAll(getMultiple(root.getChildIds(), true).stream().toList());
        return hierarchy.stream()
                .peek(OrganisationalUnit::resetCustomData)
                .map(this::setOrganisationalUnitData)
                .toList();
    }

    public OrganisationalUnit setOrganisationalUnitData(OrganisationalUnit organisationalUnit) {
        log.info(String.format("Building organisationalUnit %s", organisationalUnit.getId()));
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
        put(organisationalUnit.getId(), organisationalUnit);
        return organisationalUnit;
    }

    public OrganisationalUnit updateAgencyToken(Long organisationalUnitId, @Nullable AgencyToken newAgencyToken) {
        OrganisationalUnit organisationalUnit = get(organisationalUnitId);
        organisationalUnit.setAgencyToken(newAgencyToken);
        rebuildHierarchy(organisationalUnit);
        return organisationalUnit;
    }

}
