package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LearningTagOverview {

    private Long id;
    private String urlSlug;
    private String name;
}
