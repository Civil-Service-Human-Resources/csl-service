package uk.gov.cabinetoffice.csl.domain.identity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GrantRequest {
    public String grant_type;
}
