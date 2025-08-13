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
public class CourseCompletionWithOrganisationAggregation extends CourseCompletionAggregation {

    private Long organisationId;

    public CourseCompletionWithOrganisationAggregation(LocalDateTime dateBin, Integer total, String courseId, Long organisationId) {
        super(dateBin, total, courseId);
        this.organisationId = organisationId;
    }
}
