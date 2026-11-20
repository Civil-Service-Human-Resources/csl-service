package uk.gov.cabinetoffice.csl.controller.admin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.ModuleOverview;

import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminFaceToFaceModuleOverview extends ModuleOverview {

    private LocalDateTime earliestDate;
    private Collection<EventOverview> events;

    public AdminFaceToFaceModuleOverview(String id, String title, String description, Boolean mandatory,
                                         Integer duration, Integer costInPounds, LocalDateTime earliestDate,
                                         Collection<EventOverview> events) {
        super(id, title, description, mandatory, false, ModuleType.facetoface, duration, costInPounds, null, false);
        this.earliestDate = earliestDate;
        this.events = events;
    }
}
