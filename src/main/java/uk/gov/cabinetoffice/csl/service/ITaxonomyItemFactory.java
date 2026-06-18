package uk.gov.cabinetoffice.csl.service;

import uk.gov.cabinetoffice.csl.domain.taxonomy.ITaxonomyItem;

public interface ITaxonomyItemFactory<T extends ITaxonomyItem, Overview> {
    Overview createOverview(T object);
}
