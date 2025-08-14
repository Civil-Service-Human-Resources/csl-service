package uk.gov.cabinetoffice.csl.domain.reportservice.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CourseCompletionWithOrganisationAggregation extends CourseCompletionAggregation implements IAggregation {

    private Long organisationId;

    public String getUniqueString() {
        return String.format("%s,%s,%s,%s", dateBin, organisationId, getCourseId(), getTotal());
    }
}
