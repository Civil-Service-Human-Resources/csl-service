package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import uk.gov.cabinetoffice.csl.util.IFetchClient;

import java.io.Serializable;

public abstract class LearningCatalogueCacheFetchClient<T extends Serializable> implements IFetchClient<T> {

    protected final ILearningCatalogueClient client;

    protected LearningCatalogueCacheFetchClient(ILearningCatalogueClient client) {
        this.client = client;
    }
}
