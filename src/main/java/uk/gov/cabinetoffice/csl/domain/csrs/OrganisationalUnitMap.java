package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyNode;
import uk.gov.cabinetoffice.csl.domain.taxonomy.TaxonomyMap;

import java.util.List;

@Slf4j
public class OrganisationalUnitMap extends TaxonomyMap<OrganisationalUnit, BasicTaxonomyNode> {

    public static OrganisationalUnitMap buildFromList(List<OrganisationalUnit> organisationalUnits) {
        OrganisationalUnitMap map = new OrganisationalUnitMap();
        for (OrganisationalUnit organisationalUnit : organisationalUnits) {
            map.put(organisationalUnit.getId(), organisationalUnit);
        }
        organisationalUnits.forEach(map::setData);
        return map;
    }

    @Override
    protected BasicTaxonomyNode buildNode(OrganisationalUnit object) {
        return new BasicTaxonomyNode(object.getName(), object.getId());
    }

    public OrganisationalUnit setData(OrganisationalUnit organisationalUnit) {
        log.info("Building organisationalUnit {}", organisationalUnit.getId());
        StringBuilder formattedName = new StringBuilder(organisationalUnit.getNameWithAbbreviation());
        StringBuilder formattedNameWithoutAbbreviation = new StringBuilder(organisationalUnit.getName());
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
            formattedNameWithoutAbbreviation.insert(0, parentOrganisationalUnit.getName() + " | ");
            parentId = parentOrganisationalUnit.getParentId();
        }
        organisationalUnit.setFormattedName(formattedName.toString());
        organisationalUnit.setFormattedNameWithoutAbbreviation(formattedNameWithoutAbbreviation.toString());
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
