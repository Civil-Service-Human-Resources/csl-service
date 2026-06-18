package uk.gov.cabinetoffice.csl.domain.taxonomy;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;

public interface ITaxonomyItem {
    Collection<Long> getChildIds();

    String getName();

    Long getId();

    Long getParentId();

    void setParentId(Long parentId);

    void resetCustomData();

    @JsonIgnore
    default void addChildId(Long childId) {
        getChildIds().add(childId);
    }
}
