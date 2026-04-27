package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PartialSuggestedLearningSection {

    private String title;
    private Collection<String> courseIds;

}
