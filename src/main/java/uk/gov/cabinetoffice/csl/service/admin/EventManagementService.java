package uk.gov.cabinetoffice.csl.service.admin;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.controller.admin.model.EventOverview;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.util.List;
import java.util.Map;

@Service
public class EventManagementService {

    private final LearningCatalogueService learningCatalogueService;
    private final ILearnerRecordClient learnerRecordClient;
    private final UserDetailsService userDetailsService;
    private final AdminManagementFactory adminManagementFactory;

    public EventManagementService(LearningCatalogueService learningCatalogueService, ILearnerRecordClient learnerRecordClient, UserDetailsService userDetailsService, AdminManagementFactory adminManagementFactory) {
        this.learningCatalogueService = learningCatalogueService;
        this.learnerRecordClient = learnerRecordClient;
        this.userDetailsService = userDetailsService;
        this.adminManagementFactory = adminManagementFactory;
    }

    public EventOverview getOverview(String courseId, String moduleId, String eventId) {
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        EventDto eventDto = learnerRecordClient.getEvent(eventId, true, true);
        List<String> bookedUids = eventDto.getActiveBookings().stream().map(BookingDto::getLearner).toList();
        Map<String, String> uidsToEmails = userDetailsService.fetchEmailsByUids(bookedUids);
        return adminManagementFactory.createEventOverview(courseWithModuleWithEvent, eventDto, uidsToEmails);
    }
}
