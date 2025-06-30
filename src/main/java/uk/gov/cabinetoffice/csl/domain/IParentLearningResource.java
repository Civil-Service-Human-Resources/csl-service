package uk.gov.cabinetoffice.csl.domain;

import java.util.Collection;

public interface IParentLearningResource<C extends IChildLearningResource> extends ILearningResource {
    Collection<C> getChildren();
    
}
