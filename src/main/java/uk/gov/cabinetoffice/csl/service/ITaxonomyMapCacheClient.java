package uk.gov.cabinetoffice.csl.service;

import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyNode;
import uk.gov.cabinetoffice.csl.domain.taxonomy.ITaxonomyItem;
import uk.gov.cabinetoffice.csl.domain.taxonomy.ITaxonomyItemDTO;
import uk.gov.cabinetoffice.csl.domain.taxonomy.TaxonomyMap;
import uk.gov.cabinetoffice.csl.util.IFetchClient;

public interface ITaxonomyMapCacheClient<T extends ITaxonomyItem, N extends BasicTaxonomyNode, M extends TaxonomyMap<T, N>, DTO extends ITaxonomyItemDTO> extends IFetchClient<M> {
    T create(DTO dto);

    T patch(Long id, DTO dto);
}
