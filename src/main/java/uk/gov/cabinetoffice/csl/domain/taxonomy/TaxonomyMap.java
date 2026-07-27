package uk.gov.cabinetoffice.csl.domain.taxonomy;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.error.ValidationException;

import java.util.*;
import java.util.function.Function;

@Slf4j
public abstract class TaxonomyMap<T extends ITaxonomyItem, Node extends BasicTaxonomyNode> extends HashMap<Long, T> {

    protected Node buildNodeWithChildren(T object) {
        Node node = buildNode(object);
        List<Node> childNodes = object.getChildIds().stream().map(id -> buildNodeWithChildren(get(id)))
                .sorted(Comparator.comparing(BasicTaxonomyNode::getName, String::compareToIgnoreCase)).toList();
        node.setChildren(childNodes);
        return node;
    }

    protected abstract Node buildNode(T object);

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

    public List<T> getDescendants(Long parentId) {
        List<T> parentWithDescendants = getMultiple(List.of(parentId), true);
        if (!parentWithDescendants.isEmpty()) {
            parentWithDescendants.remove(0);
        }
        return parentWithDescendants;
    }

    public List<Long> getMultipleAsIds(Collection<Long> ids, boolean includeChildren) {
        return getMultiple(ids, includeChildren).stream().map(T::getId).toList();
    }

    public List<Node> getTree() {
        return values()
                .stream().filter(o -> o.getParentId() == null)
                .map(this::buildNodeWithChildren)
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

    public List<T> getParents(Long id) {
        List<T> hierarchy = getHierarchy(id);
        if (!hierarchy.isEmpty()) {
            hierarchy.remove(0);
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

    public void validateUpdate(Long id, Long parentId) {
        Optional.ofNullable(parentId)
                .ifPresent(pId -> {
                    if (id.equals(pId)) throw new ValidationException("Can't set parent ID to self");
                    if (getMultipleAsIds(List.of(id), true).contains(pId))
                        throw new ValidationException("Can't set a parent to a child in the same hierarchy");
                });
    }

    public T updateParent(T object, Long parentId) {
        if (object.getParentId() != null) {
            update(object.getParentId(), o -> {
                o.getChildIds().remove(object.getId());
                return o;
            });
        }
        Optional.ofNullable(parentId)
                .map(newParentIdStr -> get(parentId))
                .ifPresentOrElse(newParent -> {
                    object.setParentId(newParent.getId());
                    object.setParentName(newParent.getName());
                }, () -> {
                    object.setParentId(null);
                    object.setParentName(null);
                });
        return object;
    }

}
