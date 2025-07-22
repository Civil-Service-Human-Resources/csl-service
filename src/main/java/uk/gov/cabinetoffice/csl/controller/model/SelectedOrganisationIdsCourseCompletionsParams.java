package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectedOrganisationIdsCourseCompletionsParams extends CourseCompletionsParams{
    @Size(min = 1)
    protected List<String> selectedOrganisationIds;
}
