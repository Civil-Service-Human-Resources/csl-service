package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalValue {
    private String item;
    private String value;
}
