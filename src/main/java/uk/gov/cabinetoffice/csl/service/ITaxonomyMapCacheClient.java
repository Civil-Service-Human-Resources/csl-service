package uk.gov.cabinetoffice.csl.service;

import uk.gov.cabinetoffice.csl.domain.TaxonomyMap;
import uk.gov.cabinetoffice.csl.domain.taxonomy.ITaxonomyItem;
import uk.gov.cabinetoffice.csl.domain.taxonomy.ITaxonomyItemDTO;
import uk.gov.cabinetoffice.csl.util.IFetchClient;

public interface ITaxonomyMapCacheClient<T extends ITaxonomyItem, M extends TaxonomyMap<T>, DTO extends ITaxonomyItemDTO> extends IFetchClient<M> {
    T create(DTO dto);
}
