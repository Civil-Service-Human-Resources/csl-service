package uk.gov.cabinetoffice.csl.domain.error;

public class LearningCatalogueResourceNotFoundException extends RuntimeException {
    public LearningCatalogueResourceNotFoundException(String message) {
        super(message);
    }
}
