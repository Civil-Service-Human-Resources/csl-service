package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagMap;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagTreeNode;
import uk.gov.cabinetoffice.csl.service.ITaxonomyMapCacheClient;

public interface ILearningTagMapClient extends ITaxonomyMapCacheClient<LearningTag, LearningTagTreeNode, LearningTagMap, LearningTagDTO> {
}
