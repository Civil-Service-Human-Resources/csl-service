package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ModuleType {
    elearning("elearning"),
    file("file"),
    link("link"),
    video("video"),
    facetoface("face-to-face");

    private final String text;

    ModuleType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @JsonValue
    public String getName() {
        return text;
    }
}
