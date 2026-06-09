package uk.gov.cabinetoffice.csl.service.report.params;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
public class GetCourseCompletionAggregationParams {
    private LocalDateTime from;
    private LocalDateTime to;
    private Collection<Integer> professionIds;
    private Integer size;
    private Collection<String> excludeIds;
}
