package uk.gov.cabinetoffice.csl.domain.learning;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learning.DisplayModuleSummary;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RequiredDisplayCourse extends DisplayCourse {

    private final DisplayAudience audience;

    public RequiredDisplayCourse(String courseId, String courseTitle, String shortDescription, LocalDateTime lastUpdated, LocalDateTime completionDate, State status, Collection<DisplayModule> modules, Integer requiredModules, Integer completedRequiredModules, DisplayAudience audience) {
        super(courseId, courseTitle, shortDescription, lastUpdated, completionDate, status, modules, requiredModules, completedRequiredModules);
        this.audience = audience;
    }

    public static RequiredDisplayCourse build(Course course, Collection<DisplayModule> modules, DisplayModuleSummary moduleSummary, DisplayAudience audience,
                                              LocalDateTime lastUpdated) {
        return new RequiredDisplayCourse(course.getCacheableId(), course.getTitle(), course.getShortDescription(), lastUpdated,
                moduleSummary.getCompletionDate(), moduleSummary.getStatus(),
                modules, moduleSummary.getRequiredForCompletionCount(), moduleSummary.getRequiredCompletedCount(), audience);
    }
}
