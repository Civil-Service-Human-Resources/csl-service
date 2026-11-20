package uk.gov.cabinetoffice.csl.service.admin;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.admin.model.*;
import uk.gov.cabinetoffice.csl.domain.csrs.Grade;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.invite.InviteDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.ModuleOverview;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminManagementFactory {

    private final OrganisationalUnitService organisationalUnitService;
    private final CivilServantRegistryService civilServantRegistryService;

    public AdminManagementFactory(OrganisationalUnitService organisationalUnitService, CivilServantRegistryService civilServantRegistryService) {
        this.organisationalUnitService = organisationalUnitService;
        this.civilServantRegistryService = civilServantRegistryService;
    }

    public EventOverview createEventOverview(CourseWithModuleWithEvent courseWithModuleWithEvent, EventDto eventDto, Map<String, String> uidsToEmails) {
        List<BookingOverview> bookings = eventDto.getActiveBookings()
                .stream().map(b -> new BookingOverview(b.getId(), b.getBookingReference(),
                        uidsToEmails.get(b.getLearner()), b.getStatus(), b.getPoNumber()))
                .filter(b -> b.getLearnerEmail() != null)
                .toList();
        Course course = courseWithModuleWithEvent.getCourse();
        Module module = courseWithModuleWithEvent.getModule();
        Event event = courseWithModuleWithEvent.getEvent();
        Venue venue = event.getVenue();
        venue.setAvailability(venue.getCapacity() - eventDto.getActiveBookings().size());
        String cancellationReason = event.getCancellationReason() != null ? event.getCancellationReason().getValue() : null;
        return new EventOverview(event.getId(), event.getVenue(), event.getDateRangesAsStrings(), event.getStatus().getValue(),
                cancellationReason, module.getId(), module.getTitle(), course.getId(), course.getTitle(), course.getStatus().getName(),
                eventDto.getInvites().stream().map(InviteDto::getLearnerEmail).toList(), bookings);
    }

    private AdminFaceToFaceModuleOverview createAdminFaceToFaceModuleOverview(Module faceToFaceModule) {
        Collection<EventOverview> eventOverviews = faceToFaceModule.getEvents()
                .stream().map(e -> EventOverview.builder()
                        .venue(e.getVenue())
                        .dates(e.getDateRangesAsStrings())
                        .id(e.getId()).build()).toList();
        return new AdminFaceToFaceModuleOverview(faceToFaceModule.getId(), faceToFaceModule.getTitle(), faceToFaceModule.getDescription(),
                !faceToFaceModule.isOptional(), faceToFaceModule.getDurationInSeconds(), faceToFaceModule.getCost().intValue(),
                eventOverviews);
    }

    private Collection<AdminAudienceOverview> createAdminAudienceOverviews(Collection<Audience> audiences) {
        Set<String> departmentCodes = new HashSet<>();
        Set<String> gradeCodes = new HashSet<>();
        audiences.forEach(a -> {
            departmentCodes.addAll(a.getDepartments());
            gradeCodes.addAll(a.getGrades());
        });
        Map<String, String> organisationCodeToNameMap = organisationalUnitService.getOrganisationalUnitsWithCodes(departmentCodes)
                .stream().collect(Collectors.toMap(OrganisationalUnit::getCode, OrganisationalUnit::getFormattedName));
        Map<String, String> gradeCodeToNameMap = civilServantRegistryService.getGrades()
                .stream().filter(g -> gradeCodes.contains(g.getCode())).collect(Collectors.toMap(Grade::getCode, Grade::getName));
        return audiences.stream().map(a -> {
            List<String> orgNames = a.getDepartments().stream().map(organisationCodeToNameMap::get).filter(Objects::nonNull).sorted().toList();
            List<String> gradeNames = a.getGrades().stream().map(gradeCodeToNameMap::get).filter(Objects::nonNull).sorted().toList();
            return new AdminAudienceOverview(a.getName(), a.getType(), orgNames, gradeNames, a.getAreasOfWork(), a.getInterests(), a.getRequiredByAsDateString());
        }).toList();
    }

    public AdminCourseOverview createCourseOverview(Course course) {
        List<ModuleOverview> modules = new ArrayList<>();
        List<AdminFaceToFaceModuleOverview> faceToFaceModules = new ArrayList<>();
        course.getModules().forEach(m -> {
            if (m.getModuleType().equals(ModuleType.facetoface)) {
                faceToFaceModules.add(createAdminFaceToFaceModuleOverview(m));
            }
            modules.add(ModuleOverview.build(m, null));
        });
        Collection<AdminAudienceOverview> adminAudienceOverviews = createAdminAudienceOverviews(course.getAudiences());
        return new AdminCourseOverview(course, course.getOrderedLearningTags(), modules, faceToFaceModules, adminAudienceOverviews);
    }
}
