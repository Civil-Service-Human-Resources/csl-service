package uk.gov.cabinetoffice.csl.domain.error;

import org.webjars.NotFoundException;

public class LearningCatalogueResourceNotFoundException extends NotFoundException {
    public LearningCatalogueResourceNotFoundException(String message) {
        super(message);
    }
}
