package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormattedOrganisationalUnitName implements Serializable {
    private Long id;
    private String name;
    private String code;
    private String abbreviation;
}
