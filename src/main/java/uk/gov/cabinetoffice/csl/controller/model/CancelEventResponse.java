package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CancelEventResponse {

    private String courseId;
    private String moduleId;
    private String eventId;
    private Collection<String> learners;
}
