package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learning.model.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTag;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagMap;
import uk.gov.cabinetoffice.csl.service.CachedTaxonomyMapService;
import uk.gov.cabinetoffice.csl.service.ITaxonomyItemFactory;
import uk.gov.cabinetoffice.csl.service.ITaxonomyMapCacheClient;
import uk.gov.cabinetoffice.csl.util.IUtilService;

@Service
public class LearningTagMapService extends CachedTaxonomyMapService<LearningTag, LearningTagMap, LearningTagDTO, LearningTagOverview> {

    private final Integer maxUrlSlugSize;
    private final IUtilService utilService;

    public LearningTagMapService(@Qualifier("learningCatalogue") Cache cache,
                                 ITaxonomyItemFactory<LearningTag, LearningTagOverview> taxonomyItemFactory,
                                 ITaxonomyMapCacheClient<LearningTag, LearningTagMap, LearningTagDTO> client,
                                 @Value("${learningCatalogue.validation.learningTag.maxUrlSize}") Integer maxUrlSlugSize, IUtilService utilService) {
        super(cache, "learningTagMap", LearningTagMap.class, taxonomyItemFactory, client);
        this.maxUrlSlugSize = maxUrlSlugSize;
        this.utilService = utilService;
    }

    @Override
    public LearningTagOverview create(LearningTagDTO dto) {
        if (dto.getUrlSlug() == null) {
            String slug = utilService.generateUrlSlugFromString(dto.getName(), maxUrlSlugSize);
            dto.setUrlSlug(slug);
        }
        return super.create(dto);
    }
}
