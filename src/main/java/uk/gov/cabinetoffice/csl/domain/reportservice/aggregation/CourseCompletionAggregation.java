package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CourseCompletionAggregation extends Aggregation implements IAggregation {

    private String courseId;

    public CourseCompletionAggregation(LocalDateTime dateBin, Integer total, String courseId) {
        super(dateBin, total);
        this.courseId = courseId;
    }
}
