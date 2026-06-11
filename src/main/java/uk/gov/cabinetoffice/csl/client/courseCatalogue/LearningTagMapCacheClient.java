package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagMap;

@Component
public class LearningTagMapCacheClient extends LearningCatalogueCacheFetchClient<LearningTagMap> {
    protected LearningTagMapCacheClient(ILearningCatalogueClient client) {
        super(client);
    }

    @Override
    public LearningTagMap fetch() {
        return LearningTagMap.buildFromList(client.getAllLearningTags());
    }
}
