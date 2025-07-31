package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class OrganisationIdsCourseCompletionsParams extends CourseCompletionsParams {
    @Size(min = 1)
    protected List<String> organisationIds;

    public void setOrganisationIds(List<Long> organisationIds) {
        this.organisationIds = organisationIds.stream().map(Object::toString).toList();
    }
}
