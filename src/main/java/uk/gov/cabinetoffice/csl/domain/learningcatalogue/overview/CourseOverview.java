package uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagOverview;

import java.util.Collection;
import java.util.List;
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

    public CourseOverview(Course course, List<LearningTagOverview> orderedTags, LearningPlan learningPlan,
                          Collection<? extends ModuleOverview> modules, Integer mandatoryCount) {
        this(course.getId(), course.getTitle(), course.getDescription(), course.getLearningOutcomes(), course.getStatus(), learningPlan,
                course.getCourseType(), course.getDurationInSeconds(), orderedTags, course.getGrades(), course.getAreasOfWork(), course.getCost(), modules,
                mandatoryCount);
    }
}
