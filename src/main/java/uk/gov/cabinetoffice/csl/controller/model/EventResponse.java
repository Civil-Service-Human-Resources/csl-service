package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.EventModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private String message;
    private String courseTitle;
    private String moduleTitle;
    private String courseId;
    private String moduleId;
    private String eventId;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate eventTimestamp;

    public static EventResponse fromMetaData(EventModuleRecordAction actionType, CourseWithModuleWithEvent courseWithModuleWithEvent) {
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        return new EventResponse(String.format("Successfully applied action '%s' to course record", actionType.getDescription()), course.getTitle(),
                module.getTitle(), course.getCacheableId(), module.getId(), event.getId(), event.getStartTime());
    }
}
