package uk.gov.cabinetoffice.csl.domain.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormattedTaxonomyItems<T extends IFormattedTaxonomyItem> implements IFormattedTaxonomyItems<T> {

    private List<T> names;

}
