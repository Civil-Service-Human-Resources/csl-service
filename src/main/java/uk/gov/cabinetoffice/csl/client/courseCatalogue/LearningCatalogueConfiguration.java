package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "learning-catalogue")
@RequiredArgsConstructor
@Valid
@Validated
public class LearningCatalogueConfiguration {
    @NotNull
    private final String serviceUrl;
    @NotNull
    private final String learningTagUrl;
    @NotNull
    private final String learningTagStateUrl;
    @NotNull
    private final Integer learningTagMaxPageSize;
    @NotNull
    private final String courseUrl;
    @NotNull
    private final Integer courseBatchSize;
    @NotNull
    private final String courseV2Url;
    @NotNull
    private final String courseV2SearchUrl;

    public String getLearningTagUrl(Long id) {
        return learningTagUrl + "/" + id;
    }
}
