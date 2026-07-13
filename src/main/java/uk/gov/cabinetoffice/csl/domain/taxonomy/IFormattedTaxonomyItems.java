package uk.gov.cabinetoffice.csl.domain.taxonomy;

import java.io.Serializable;
import java.util.List;

public interface IFormattedTaxonomyItems<T extends IFormattedTaxonomyItem> extends Serializable {
    List<T> getNames();
}
