package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectedOrganisationIdsCourseCompletionsParams extends CourseCompletionsParams {
    @Size(min = 1)
    protected List<Long> selectedOrganisationIds;

    public List<Long> getSelectedOrganisationIds() {
        return this.selectedOrganisationIds == null ? new ArrayList<>() : this.selectedOrganisationIds;
    }
}
