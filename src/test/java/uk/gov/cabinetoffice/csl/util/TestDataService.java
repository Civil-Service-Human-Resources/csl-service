package uk.gov.cabinetoffice.csl.util;

import lombok.Getter;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.rustici.*;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final AreaOfWork profession = new AreaOfWork(3L, "DDaT");


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
        for (int i = 0; i < moduleCount; i++) {
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
                LocalDate.now().plusDays(1L), null);
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

    public OrganisationalUnitsPagedResponse generateOrganisationalUnitsPagedResponse() {
        OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse = new OrganisationalUnitsPagedResponse();
        organisationalUnitsPagedResponse.setContent(createOrganisationsList());
        organisationalUnitsPagedResponse.setLast(true);
        organisationalUnitsPagedResponse.setNumber(0);
        organisationalUnitsPagedResponse.setTotalPages(1);
        organisationalUnitsPagedResponse.setTotalElements(6);
        organisationalUnitsPagedResponse.setSize(10);
        return organisationalUnitsPagedResponse;
    }

    private List<OrganisationalUnit> createOrganisationsList() {
        List<OrganisationalUnit> organisationalUnits = new ArrayList<>();

        OrganisationalUnit organisationalUnits1 = new OrganisationalUnit();
        organisationalUnits1.setId(1L);
        organisationalUnits1.setName("OrgName1");
        organisationalUnits1.setParentId(null);
        organisationalUnits1.setAbbreviation("OName1");
        organisationalUnits1.setCode("ON1");
        organisationalUnits.add(organisationalUnits1);

        OrganisationalUnit organisationalUnits2 = new OrganisationalUnit();
        organisationalUnits2.setId(2L);
        organisationalUnits2.setName("OrgName2");
        organisationalUnits2.setParentId(1L);
        organisationalUnits2.setCode("ON2");
        organisationalUnits2.setDomains(List.of(new Domain(1L, "domain2.com", LocalDateTime.of(2025, 1, 1, 10, 0, 0))));
        organisationalUnits.add(organisationalUnits2);

        OrganisationalUnit organisationalUnits3 = new OrganisationalUnit();
        organisationalUnits3.setId(3L);
        organisationalUnits3.setName("OrgName3");
        organisationalUnits3.setParentId(2L);
        organisationalUnits3.setAbbreviation("OName3");
        organisationalUnits3.setCode("ON3");
        organisationalUnits.add(organisationalUnits3);

        OrganisationalUnit organisationalUnits4 = new OrganisationalUnit();
        organisationalUnits4.setId(4L);
        organisationalUnits4.setName("OrgName4");
        organisationalUnits4.setParentId(3L);
        organisationalUnits4.setAbbreviation("OName4");
        organisationalUnits4.setCode("ON4");
        organisationalUnits.add(organisationalUnits4);

        OrganisationalUnit organisationalUnits5 = new OrganisationalUnit();
        organisationalUnits5.setId(5L);
        organisationalUnits5.setName("OrgName5");
        organisationalUnits5.setParentId(1L);
        organisationalUnits5.setAbbreviation("OName5");
        organisationalUnits5.setCode("ON5");
        organisationalUnits.add(organisationalUnits5);

        OrganisationalUnit organisationalUnits6 = new OrganisationalUnit();
        organisationalUnits6.setId(6L);
        organisationalUnits6.setName("OrgName6");
        organisationalUnits6.setAbbreviation("OName6");
        organisationalUnits6.setCode("ON6");
        organisationalUnits.add(organisationalUnits6);

        return organisationalUnits;
    }
}
