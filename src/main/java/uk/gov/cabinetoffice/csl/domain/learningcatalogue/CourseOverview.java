package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagOverview;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CourseOverview {
    private String id;
    private String title;
    private String description;
    private String learningOutcomes;
    private String type;
    private Integer duration;
    private Collection<LearningTagOverview> tags;
    private Collection<String> grades;
    private Integer costInPounds;
    private Collection<ModuleOverview> modules;

}
