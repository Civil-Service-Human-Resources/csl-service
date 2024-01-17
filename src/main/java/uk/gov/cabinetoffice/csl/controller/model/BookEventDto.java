package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookEventDto {

    private List<String> accessibilityOptions = Collections.emptyList();
    private String poNumber;
    @NotNull
    @NotEmpty
    private String learnerEmail;
    @NotNull
    @NotEmpty
    private String learnerName;
}
