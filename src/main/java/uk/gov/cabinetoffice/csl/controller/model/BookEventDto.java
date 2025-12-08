package uk.gov.cabinetoffice.csl.controller.model;

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
}
