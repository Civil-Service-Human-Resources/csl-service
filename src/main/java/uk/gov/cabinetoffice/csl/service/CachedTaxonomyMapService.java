package uk.gov.cabinetoffice.csl.service;

import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.domain.taxonomy.*;
import uk.gov.cabinetoffice.csl.util.BasicFetchedCache;

import java.util.Objects;
import java.util.function.Consumer;

public class CachedTaxonomyMapService<Item extends ITaxonomyItem, Node extends BasicTaxonomyNode, Map extends TaxonomyMap<Item, Node>,
        DTO extends ITaxonomyItemDTO, Overview> extends BasicFetchedCache<Map> {

    protected final ITaxonomyItemFactory<Item, Overview> taxonomyItemFactory;
    protected final ITaxonomyMapCacheClient<Item, Node, Map, DTO> client;

    public CachedTaxonomyMapService(Cache cache, String singleId, Class<Map> clazz, ITaxonomyItemFactory<Item, Overview> taxonomyItemFactory, ITaxonomyMapCacheClient<Item, Node, Map, DTO> client) {
        super(cache, singleId, clazz, client);
        this.taxonomyItemFactory = taxonomyItemFactory;
        this.client = client;
    }

    public BasicTaxonomyTree getTree() {
        return new BasicTaxonomyTree(get().getTree());
    }

    public Overview getOverview(Long id) {
        Item object = get().get(id);
        return taxonomyItemFactory.createOverview(object);
    }

    protected void updateMap(Consumer<Map> updateFn) {
        Map map = get();
        updateFn.accept(map);
        put(map);
    }

    public Overview create(DTO dto) {
        Item object = client.create(dto);
        Map map = get();
        object.setParentId(dto.getParentId());
        object = map.setData(object);
        put(map);
        return taxonomyItemFactory.createOverview(object);
    }

    public Overview update(Long id, DTO dto) {
        Map map = get();
        Item object = map.get(id);
        map.validateUpdate(id, dto.getParentId());
        client.patch(id, dto);
        if (!Objects.equals(object.getParentId(), dto.getParentId())) {
            object = map.updateParent(object, dto.getParentId());
        }
        updateObjectWithDto(object, dto);
        map.rebuildHierarchy(object);
        put(map);
        return taxonomyItemFactory.createOverview(object);
    }

    protected void updateObjectWithDto(Item object, DTO dto) {
        object.setName(dto.getName());
        object.setCode(dto.getCode());
    }
}
