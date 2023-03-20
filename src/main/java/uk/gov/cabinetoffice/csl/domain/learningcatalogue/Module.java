package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Module  implements Serializable {
    private String id;
    private String title;
    private String moduleType;
    private Long duration;
    private BigDecimal cost;
    private Collection<Event> events;
    private boolean optional;
}
