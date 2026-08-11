package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.taxonomy.TaxonomyMap;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LearningTagMap extends TaxonomyMap<LearningTag, LearningTagTreeNode> {

    public static LearningTagMap buildFromList(List<LearningTag> learningTags) {
        LearningTagMap map = new LearningTagMap();
        for (LearningTag learningTag : learningTags) {
            map.put(learningTag.getId(), learningTag);
        }
        learningTags.forEach(map::setData);
        return map;
    }

    @Override
    protected LearningTagTreeNode buildNode(LearningTag object) {
        return new LearningTagTreeNode(object.getName(), object.getId(), new ArrayList<>(), object.isArchived());
    }

    @Override
    public LearningTag setData(LearningTag learningTag) {
        log.info("Building learning tag {}", learningTag.getId());
        StringBuilder formattedName = new StringBuilder(learningTag.getName());
        StringBuilder fullUrl = new StringBuilder(learningTag.getUrlSlug());
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
            fullUrl.insert(0, parentLearningTag.getUrlSlug() + "/");
            parentId = parentLearningTag.getParentId();
        }
        learningTag.setFormattedName(formattedName.toString());
        learningTag.setFullUrl(fullUrl.toString());
        put(learningTag.getId(), learningTag);
        return learningTag;
    }
}
