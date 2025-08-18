package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HalObject implements Serializable {
    private Long id;
    private String code;
    private String name;

    @JsonProperty("_links")
    private void unpackLinks(Map<String, Map<String, String>> links) {
        String href = links.get("self").get("href");
        this.id = Long.parseLong(href.substring(href.lastIndexOf('/') + 1));
    }
}
