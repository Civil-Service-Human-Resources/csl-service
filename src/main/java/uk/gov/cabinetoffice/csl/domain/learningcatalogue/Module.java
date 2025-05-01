package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.IChildLearningResource;
import uk.gov.cabinetoffice.csl.domain.IParentLearningResource;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Module implements IChildLearningResource, IParentLearningResource<Event>, Cacheable {
    private String id;
    private String courseId;
    private String title;
    private ModuleType moduleType;
    private String description;
    private Long duration;
    private BigDecimal cost;
    private Collection<Event> events = Collections.emptyList();
    private boolean optional;
    private String url;

    private boolean requiredForCompletion;

    @JsonIgnore
    public void updateEvent(Event event) {
        events.stream().collect(Collectors.toMap(Event::getId, e -> e)).put(event.getId(), event);
    }

    @JsonIgnore
    public Event getEvent(String eventId) {
        List<Event> events = this.events.stream().filter(e -> e.getId().equals(eventId)).toList();
        if (events.size() != 1) {
            return null;
        } else {
            return events.get(0);
        }
    }

    @JsonIgnore
    public boolean isFree() {
        return this.cost.signum() == 0;
    }

    @JsonIgnore
    public boolean isType(ModuleType type) {
        return this.getModuleType().equals(type);
    }

    @Override
    public String getParentId() {
        return courseId;
    }

    @Override
    public String getResourceId() {
        return id;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public LearningResourceType getType() {
        return LearningResourceType.MODULE;
    }

    @Override
    public Collection<Event> getChildren() {
        return this.events;
    }

    @Override
    public String getCacheableId() {
        return id;
    }
}
