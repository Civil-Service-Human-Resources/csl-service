package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagOverview;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CourseOverview {
    private String id;
    private String title;
    private String description;
    private String learningOutcomes;
    private CourseStatus status;
    private LearningPlan isInLearningPlan;
    private String type;
    private Integer duration;
    private Collection<LearningTagOverview> learningTags;
    private Set<String> grades;
    private Set<String> areasOfWork;
    private Integer costInPounds;
    private Collection<? extends ModuleOverview> modules;
    private Integer mandatoryModuleCount;
}
