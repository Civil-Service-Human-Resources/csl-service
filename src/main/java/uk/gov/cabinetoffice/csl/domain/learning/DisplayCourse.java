package uk.gov.cabinetoffice.csl.domain.learning;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learning.DisplayModuleSummary;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@RequiredArgsConstructor
public class DisplayCourse {

    private final String courseId;
    private final String courseTitle;
    private final String shortDescription;
    private final LocalDateTime lastUpdated;
    private final LocalDateTime completionDate;
    private final State status;
    private final DisplayAudience audience;
    private final Collection<DisplayModule> modules;
    private final Integer requiredModules;
    private final Integer completedRequiredModules;

    public static DisplayCourse build(Course course, Collection<DisplayModule> modules, DisplayModuleSummary moduleSummary, DisplayAudience audience,
                                      LocalDateTime lastUpdated) {
        return new DisplayCourse(course.getCacheableId(), course.getTitle(), course.getShortDescription(), lastUpdated,
                moduleSummary.getCompletionDate(), moduleSummary.getStatus(), audience,
                modules, moduleSummary.getRequiredForCompletionCount(), moduleSummary.getRequiredCompletedCount());
    }

}
