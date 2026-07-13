package uk.gov.cabinetoffice.csl.domain.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormattedTaxonomyItem implements IFormattedTaxonomyItem {

    private Long id;
    private String name;
    private String code;

}
