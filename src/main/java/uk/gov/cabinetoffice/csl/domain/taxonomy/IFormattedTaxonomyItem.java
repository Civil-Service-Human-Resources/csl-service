package uk.gov.cabinetoffice.csl.domain.taxonomy;

import java.io.Serializable;

public interface IFormattedTaxonomyItem extends Serializable {
    Long getId();

    String getName();

    String getCode();
}
