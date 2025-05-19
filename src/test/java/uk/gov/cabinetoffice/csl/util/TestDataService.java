package uk.gov.cabinetoffice.csl.util;

import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private final ModuleRecordResourceId moduleRecordResourceId = new ModuleRecordResourceId(userId, moduleId);
    private final LearnerRecordResourceId courseRecordId = new LearnerRecordResourceId(userId, courseId);
    private final String userEmail = "userEmail@email.com";
    private final String learnerFirstName = "Learner";
    private final String lineManagerName = "Manager";
    private final String lineManagerEmail = "lineManager@email.com";
    private final String courseTitle = "Test Course";
    private final OrganisationalUnit grandparentOrganisationalUnit = new OrganisationalUnit(4L, "HMRC", "HMRC", "HMRC", null);
    private final OrganisationalUnit parentOrganisationalUnit = new OrganisationalUnit(3L, "Department for Work and Pensions", "DWP", "DWP", grandparentOrganisationalUnit);
    private final OrganisationalUnit organisationalUnit = new OrganisationalUnit(2L, "Cabinet Office", "CO", "CO", parentOrganisationalUnit);
    private final ArrayList<BasicOrganisationalUnit> depHierarchy = new ArrayList<>(List.of(
            new BasicOrganisationalUnit(2, "CO", "Cabinet Office"),
            new BasicOrganisationalUnit(3, "DWP", "Department for Work and Pensions"),
            new BasicOrganisationalUnit(4, "HMRC", "HMRC")
    ));
    private final Grade grade = new Grade(1L, "SEO", "Senior Executive Officer");
    private final Profession profession = new Profession(3L, "DDaT");


    public UserDetailsDto generateUserDetailsDto() {
        return new UserDetailsDto("", userEmail, learnerFirstName, 1, "professionName", lineManagerName, lineManagerEmail, 1, "gradeCode", depHierarchy);
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
        module.setRequiredForCompletion(true);
        module.setId(moduleId);
        return module;
    }

    public User generateUser() {
        return new User(
                userId,
                userEmail,
                learnerFirstName,
                profession.getId().intValue(),
                profession.getName(),
                grade.getId().intValue(),
                grade.getName(),
                lineManagerName,
                lineManagerEmail,
                depHierarchy);
    }

    public uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course generateCourse(int moduleCount) {
        uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course course =
                new uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course();
        course.setId(courseId);
        course.setTitle(courseTitle);
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i <= moduleCount; i++) {
            Module m = generateModule();
            m.setId(moduleId + i);
            modules.add(m);
        }
        course.setModules(modules);
        return course;
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

    public Audience generateRequiredAudience(String departmentCode) {
        return new Audience("audience", List.of(), List.of(departmentCode), List.of(), "P1M", Audience.Type.REQUIRED_LEARNING,
                LocalDate.now().plus(1L, ChronoUnit.DAYS), null);
    }

    public RusticiRollupData generateRusticiRollupData() {
        RusticiRollupData rollupData = new RusticiRollupData();
        Course course = new Course();
        course.setId(String.format("%s.%s", courseId, moduleId));
        Learner learner = new Learner();
        learner.setId(userId);
        learner.setFirstName(getLearnerFirstName());
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

    public CivilServant generateCivilServant() {
        return new CivilServant(getLearnerFirstName(), userEmail, userId, grade, organisationalUnit, profession, lineManagerEmail, lineManagerName);
    }
}
