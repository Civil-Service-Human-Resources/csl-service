package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Collection;

@AllArgsConstructor
@Getter
public class RequiredLearningMapFilters {

    private Collection<String> departmentCodes;
    private LocalDate dueBy;

}
