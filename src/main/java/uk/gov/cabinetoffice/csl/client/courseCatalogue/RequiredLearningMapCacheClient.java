package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;

@Component
public class RequiredLearningMapCacheClient extends LearningCatalogueCacheFetchClient<RequiredLearningMap> {
    protected RequiredLearningMapCacheClient(ILearningCatalogueClient client) {
        super(client);
    }

    @Override
    public RequiredLearningMap fetch() {
        return client.getRequiredLearningIdMap();
    }
}
