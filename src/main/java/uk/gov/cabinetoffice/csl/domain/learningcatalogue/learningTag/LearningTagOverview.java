package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LearningTagOverview implements Serializable {

    private Long id;
    private String urlSlug;
    private String name;
}
