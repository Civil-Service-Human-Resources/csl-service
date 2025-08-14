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
    protected List<Long> organisationIds;
}
