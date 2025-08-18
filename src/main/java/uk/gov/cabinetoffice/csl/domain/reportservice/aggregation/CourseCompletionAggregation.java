package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CourseCompletionAggregation extends Aggregation implements IAggregation {

    private String courseId;

    public CourseCompletionAggregation(LocalDateTime dateBin, Integer total, String courseId) {
        super(dateBin, total);
        this.courseId = courseId;
    }
}
