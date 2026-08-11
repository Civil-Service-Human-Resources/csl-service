package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.taxonomy.FormattedTaxonomyItem;

@Getter
@Setter
@NoArgsConstructor
public class FormattedOrganisationalUnitName extends FormattedTaxonomyItem {
    private String abbreviation;

    public FormattedOrganisationalUnitName(Long id, String name, String code, String abbreviation) {
        super(id, name, code);
        this.abbreviation = abbreviation;
    }

}
