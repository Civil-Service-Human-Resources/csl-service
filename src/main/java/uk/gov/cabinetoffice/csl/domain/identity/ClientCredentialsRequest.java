package uk.gov.cabinetoffice.csl.domain.identity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientCredentialsRequest {
    @JsonProperty("grant_type")
    private String grantType;
}
