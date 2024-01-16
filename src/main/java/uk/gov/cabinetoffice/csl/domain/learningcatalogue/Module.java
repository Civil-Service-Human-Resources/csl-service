package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Module implements Serializable {
    private String id;
    private String title;
    private ModuleType moduleType;
    private Long duration;
    private BigDecimal cost;
    private Collection<Event> events = Collections.emptyList();
    private boolean optional;
    private String url;

    @JsonIgnore
    public Event getEvent(String eventId) {
        List<Event> events = this.events.stream().filter(e -> e.getId().equals(eventId)).toList();
        if (events.size() != 1) {
            return null;
        } else {
            return events.get(0);
        }
    }

    public boolean isFree() {
        return this.cost.equals(BigDecimal.valueOf(0));
    }
}
