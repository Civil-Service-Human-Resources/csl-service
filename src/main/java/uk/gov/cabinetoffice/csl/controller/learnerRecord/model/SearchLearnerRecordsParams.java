package uk.gov.cabinetoffice.csl.controller.learnerRecord.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchLearnerRecordsParams {

    @NotNull
    @Size(min = 1, max = 1000)
    private Collection<String> emails;

    private LocalDateTime completedDateGte;

}
