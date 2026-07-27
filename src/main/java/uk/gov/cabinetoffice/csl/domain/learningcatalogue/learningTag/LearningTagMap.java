package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.learning.LearningTagTaxonomy;
import uk.gov.cabinetoffice.csl.domain.taxonomy.TaxonomyMap;

import java.util.*;

@Slf4j
public class LearningTagMap extends TaxonomyMap<LearningTag, LearningTagTreeNode> {

    private final Map<String, Long> urlSlugMap;

    public LearningTagMap(Map<String, Long> urlSlugMap) {
        this.urlSlugMap = urlSlugMap;
    }

    public static LearningTagMap buildFromList(List<LearningTag> learningTags) {
        LearningTagMap map = new LearningTagMap(new HashMap<>());
        for (LearningTag learningTag : learningTags) {
            map.put(learningTag.getId(), learningTag);
            map.urlSlugMap.put(learningTag.getUrlSlug(), learningTag.getId());
        }
        learningTags.forEach(map::setData);
        return map;
    }

    public LearningTag getWithUrl(String urlSlug) {
        return Optional.ofNullable(urlSlugMap.get(urlSlug))
                .map(this::get)
                .orElseThrow(() -> new IllegalArgumentException("Learning tag with not found for url: " + urlSlug));
    }

    public LearningTagTaxonomy getFullTaxonomyFromUrl(String urlSlug) {
        LearningTag learningTag = getWithUrl(urlSlug);
        Long learningTagId = learningTag.getId();
        return new LearningTagTaxonomy(learningTag, getParents(learningTagId), getDescendants(learningTagId));

    }

    @Override
    protected LearningTagTreeNode buildNode(LearningTag object) {
        return new LearningTagTreeNode(object.getName(), object.getId(), new ArrayList<>(), object.isArchived());
    }

    @Override
    public LearningTag setData(LearningTag learningTag) {
        log.info("Building learning tag {}", learningTag.getId());
        StringBuilder formattedName = new StringBuilder(learningTag.getName());
        Long parentId = learningTag.getParentId();
        int parents = 0;
        while (parentId != null) {
            LearningTag parentLearningTag = this.get(parentId);
            if (parents == 0) {
                learningTag.setParentName(parentLearningTag.getName());
                parentLearningTag.addChildId(learningTag.getId());
                parents++;
            }
            formattedName.insert(0, parentLearningTag.getName() + " | ");
            parentId = parentLearningTag.getParentId();
        }
        learningTag.setFormattedName(formattedName.toString());
        put(learningTag.getId(), learningTag);
        return learningTag;
    }
}
