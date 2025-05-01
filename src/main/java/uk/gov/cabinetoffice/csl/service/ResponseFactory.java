package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CourseResponse;
import uk.gov.cabinetoffice.csl.controller.model.ResourceActionResponse;
import uk.gov.cabinetoffice.csl.domain.ILearningResource;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.ActionWithId;

@Service
public class ResponseFactory {

    public ResourceActionResponse buildResponseFromAction(ActionWithId actionWithId, ILearningResource resource, boolean success) {
        return new ResourceActionResponse(actionWithId.getAction().getDescription(), actionWithId.getAction().getRecordType(), actionWithId.getResourceId(), resource.getName(), success);
    }

    public CourseResponse buildCourseResponseFromAction(ActionWithId actionWithId, ILearningResource resource, boolean success) {
        return new CourseResponse(actionWithId.getAction().getDescription(), actionWithId.getResourceId(), resource.getName(), success);
    }

}
