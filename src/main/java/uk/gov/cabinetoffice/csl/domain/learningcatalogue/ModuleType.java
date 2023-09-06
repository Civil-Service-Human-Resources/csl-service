package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

public enum ModuleType {
    elearning("elearning"),
    file("file"),
    link("link"),
    video("video"),
    facetoface("face-to-face");

    private final String text;

    /**
     * @param text
     */
    ModuleType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
