package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.*;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class OrganisationIdsCourseCompletionsParams extends CourseCompletionsParams {
    @Size(min = 1)
    protected List<String> organisationIds;

    @JsonIgnore
    private Map<Long, String> orgMap = new HashMap<>();

    public void setOrganisations(List<OrganisationalUnit> organisations) {
        this.organisationIds = organisations.stream()
                .peek(org -> orgMap.put(org.getId(), org.getFormattedName()))
                .map(o -> o.getId().toString()).toList();
    }

}
