package uk.gov.cabinetoffice.csl.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;

@Slf4j
public abstract class TaxonomyMap<T extends ITaxonomyItem> extends HashMap<Long, T> {

    protected BasicTaxonomyNode buildNode(T object) {
        List<BasicTaxonomyNode> childNodes = object.getChildIds().stream().map(id -> buildNode(get(id)))
                .sorted(Comparator.comparing(BasicTaxonomyNode::getName, String::compareToIgnoreCase)).toList();
        return new BasicTaxonomyNode(object.getName(), object.getId(), childNodes);
    }

    public T get(Long id) {
        return Optional.ofNullable(super.get(id)).orElseThrow(() -> new IllegalArgumentException("object not found for id: " + id));
    }

    public List<T> getMultiple(Collection<Long> ids, boolean includeChildren) {
        List<T> objects = new ArrayList<>();
        ids.forEach(id -> {
            T organisationalUnit = get(id);
            if (organisationalUnit != null) {
                objects.add(organisationalUnit);
                if (includeChildren) {
                    List<T> children = getMultiple(organisationalUnit.getChildIds(), includeChildren);
                    objects.addAll(children);
                }
            }
        });
        return objects;
    }

    public List<Long> getMultipleAsIds(Collection<Long> ids, boolean includeChildren) {
        return getMultiple(ids, includeChildren).stream().map(T::getId).toList();
    }

    public List<BasicTaxonomyNode> getTree() {
        return values()
                .stream().filter(o -> o.getParentId() == null)
                .map(this::buildNode)
                .sorted(Comparator.comparing(BasicTaxonomyNode::getName, String::compareToIgnoreCase))
                .toList();
    }

    public List<Long> delete(Collection<Long> ids) {
        return ids.stream().map(this::delete).flatMap(Collection::stream).toList();
    }

    public List<Long> delete(Long id) {
        T object = get(id);
        if (object.getParentId() != null) {
            update(object.getParentId(), o -> {
                o.getChildIds().remove(id);
                return o;
            });
        }
        List<Long> idsToRemove = getMultiple(Collections.singleton(id), true)
                .stream().map(T::getId).toList();
        idsToRemove.forEach(this::remove);
        return idsToRemove;
    }

    public T update(Long id, Function<T, T> update) {
        T object = get(id);
        if (object != null) {
            object = update.apply(object);
            put(object.getId(), object);
        }
        return object;
    }

    public List<T> getHierarchy(Long id) {
        T object = get(id);
        List<T> hierarchy = new ArrayList<>(List.of(object));
        Long parentId = object.getParentId();
        while (parentId != null) {
            T parent = get(parentId);
            hierarchy.add(parent);
            parentId = parent.getParentId();
        }
        return hierarchy;
    }

    public Map<Long, List<T>> getHierarchies(List<Long> ids) {
        Map<Long, List<T>> hierarchies = new HashMap<>();
        ids.forEach(id -> hierarchies.put(id, getHierarchy(id)));
        return hierarchies;
    }

    public List<T> rebuildHierarchy(T root) {
        ArrayList<T> hierarchy = new ArrayList<>();
        hierarchy.add(root);
        hierarchy.addAll(getMultiple(root.getChildIds(), true).stream().toList());
        return hierarchy.stream()
                .peek(T::resetCustomData)
                .map(this::setData)
                .toList();
    }

    public abstract T setData(T object);

}
