package uk.gov.cabinetoffice.csl.util;

import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Getter
public class TestDataService {

    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final String eventId = "eventId";
    private final String moduleTitle = "Test Module";
    private final String moduleUid = "uid";
    private final long moduleRecordId = 1;
    private final String userId = "userId";
    private final String useremail = "userEmail@email.com";
    private final String learnerFirstName = "Learner";
    private final String courseTitle = "Test Course";

    /**
     * Generate a course record with a blank status
     *
     * @param withModule
     * @return
     */
    public CourseRecord generateCourseRecord(boolean withModule) {
        CourseRecord cr = new CourseRecord();
        cr.setCourseId(courseId);
        cr.setUserId(userId);
        cr.setCourseTitle(courseTitle);
        if (withModule) {
            cr.setModuleRecords(List.of(generateModuleRecord()));
        }
        return cr;
    }

    public ModuleRecord generateModuleRecord() {
        ModuleRecord mr = new ModuleRecord();
        mr.setId(moduleRecordId);
        mr.setModuleId(moduleId);
        mr.setModuleTitle(moduleTitle);
        mr.setUid(moduleUid);
        return mr;
    }

    public Event generateEvent() {
        Event event = new Event();
        event.setId(eventId);
        event.setDateRanges(List.of(
                new DateRange(
                        LocalDate.of(2023, 1, 1),
                        LocalTime.of(9, 0, 0),
                        LocalTime.of(10, 0, 0)
                )
        ));
        event.setVenue(new Venue("London", "London", 10, 5));
        return event;
    }

    public Module generateModule() {
        Module module = new Module();
        module.setModuleType(ModuleType.elearning);
        module.setCost(BigDecimal.valueOf(10));
        module.setTitle(moduleTitle);
        module.setOptional(false);
        module.setId(moduleId);
        return module;
    }

    public uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course generateCourse(boolean withModule, boolean withEvent) {
        uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course course =
                new uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course();
        course.setId(courseId);
        course.setTitle(courseTitle);
        if (withModule) {
            Module m = generateModule();
            if (withEvent) {
                m.setEvents(List.of(generateEvent()));
            }
            course.setModules(List.of(m));
        }
        return course;
    }

    public RusticiRollupData generateRusticiRollupData() {
        RusticiRollupData rollupData = new RusticiRollupData();
        Course course = new Course();
        course.setId(String.format("%s/%s", courseId, moduleId));
        Learner learner = new Learner();
        learner.setId(userId);
        rollupData.setLearner(learner);
        rollupData.setCourse(course);
        rollupData.setCompletedDate(LocalDateTime.of(2023, 2, 2, 10, 0));
        return rollupData;
    }

    public LaunchLinkRequest generateLaunchLinkRequest() {
        LaunchLinkRequest launchLinkRequest = new LaunchLinkRequest();
        launchLinkRequest.setExpiry(0);
        launchLinkRequest.setRedirectOnExitUrl(String.format("ChangeMe/%s/%s", courseId, moduleId));
        return launchLinkRequest;
    }

    public RegistrationRequest generateRegistrationRequest() {
        Learner learner = new Learner();
        learner.setFirstName(learnerFirstName);
        learner.setId(userId);
        learner.setLastName("");
        Registration registration = new Registration();
        registration.setCourseId(String.format("%s.%s", courseId, moduleId));
        registration.setRegistrationId(moduleUid);
        registration.setLearner(learner);

        RegistrationRequest req = new RegistrationRequest();
        req.setRegistration(registration);
        req.setLaunchLinkRequest(generateLaunchLinkRequest());
        return req;
    }

}
