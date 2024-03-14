package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CourseWithModuleWithEvent extends CourseWithModule {
    private Event event;

    public CourseWithModuleWithEvent(CourseWithModule courseWithModule, Event event) {
        super(courseWithModule.getCourse(), courseWithModule.getModule());
        this.event = event;
    }

    public String getEventUrl() {
        return String.format("courses/%s/modules/%s/events/%s", getCourse().getId(), getModule().getId(), getEvent().getId());
    }
}
