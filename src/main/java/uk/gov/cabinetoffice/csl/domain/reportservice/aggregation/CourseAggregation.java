package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CourseAggregation implements ICourseAggregation {

    private String courseId;
    private Integer count;

}
