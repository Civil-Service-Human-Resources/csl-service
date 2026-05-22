package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CourseAggregationResponse<A extends ICourseAggregation> {
    private List<A> aggregations;
}
