package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseRecordActionServiceFactory {

    private final Map<CourseRecordAction, CourseActionService> serviceMap;

    public CourseRecordActionServiceFactory(List<CourseActionService> services) {
        serviceMap = new HashMap<>();
        services.forEach(service -> serviceMap.put(service.getType(), service));
    }

    public CourseActionService getService(CourseRecordAction action) {
        CourseActionService service = this.serviceMap.get(action);
        if (service == null) {
            throw new GenericServerException(String.format("Failed to get CourseActionService of type '%s'", action));
        }
        return service;
    }
}
