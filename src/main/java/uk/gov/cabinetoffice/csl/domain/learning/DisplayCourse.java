package uk.gov.cabinetoffice.csl.domain.learning;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;
import java.util.List;

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
    private final List<DisplayModule> modules;
    private final Integer requiredModules;
    private final Integer completedRequiredModules;

}
