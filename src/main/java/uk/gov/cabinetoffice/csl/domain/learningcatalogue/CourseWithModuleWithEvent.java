package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

@Getter
@Setter
public class CourseWithModuleWithEvent extends CourseWithModule {
    private final Event event;

    public CourseWithModuleWithEvent(CourseWithModule courseWithModule, Event event) {
        super(courseWithModule.getCourse(), courseWithModule.getModule());
        this.event = event;
    }
    
    public String getEventUrl() {
        return String.format("courses/%s/modules/%s/events/%s", getCourse().getCacheableId(), getModule().getId(), getEvent().getId());
    }

}
