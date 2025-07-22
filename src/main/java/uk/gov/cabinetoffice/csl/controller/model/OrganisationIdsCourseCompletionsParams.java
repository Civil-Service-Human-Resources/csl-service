package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class OrganisationIdsCourseCompletionsParams extends CourseCompletionsParams{
    @Size(min = 1)
    protected List<String> organisationIds;
}
