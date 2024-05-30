package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookEventDto {
    private List<String> accessibilityOptions = Collections.emptyList();
    private String poNumber;
    @Valid
    private UserDetailsDto userDetailsDto;
}
