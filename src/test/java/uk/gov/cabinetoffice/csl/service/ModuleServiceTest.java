package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordActionFactory;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleServiceTest extends TestDataService {

    @Mock
    private ModuleActionService moduleActionService;

    @Mock
    private ModuleRecordActionFactory moduleRecordActionFactory;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RusticiService rusticiService;

    @InjectMocks
    private ModuleService moduleService;


    private final Course course = generateCourse(true, false);
    private final User user = generateUser();

    @BeforeEach
    public void setup() {
        reset();
    }

    @Test
    public void shouldLaunchModule() {
        Module module = course.getModule(getModuleId());
        module.setModuleType(ModuleType.link);
        module.setUrl("https://test.com");
        CourseWithModule courseWithModule = mockCatalogueCall(course, module);
        LaunchLink result = moduleService.launchModule(getUserId(), getCourseId(), getModuleId());
        verify(moduleActionService, atMostOnce()).completeModule(courseWithModule, getUserId());
        assertEquals("https://test.com", result.getLaunchLink());
    }

    @Test
    public void shouldLaunchELearning() {
        Module module = course.getModule(getModuleId());
        module.setModuleType(ModuleType.elearning);
        ModuleRecord moduleRecord = generateModuleRecord();
        moduleRecord.setUid("uid");
        CourseWithModule courseWithModule = mockCatalogueCall(course, module);
        when(moduleActionService.launchModule(courseWithModule, getUserId())).thenReturn(moduleRecord);
        LaunchLink launchLink = createLaunchLink();
        RegistrationInput registrationInput = new RegistrationInput("uid", getCourseId(), getModuleId(), getUserId(), getLearnerFirstName(), "");
        when(rusticiService.createLaunchLink(registrationInput)).thenReturn(launchLink);
        when(userDetailsService.getUserWithUid(getUserId())).thenReturn(user);
        LaunchLink result = moduleService.launchModule(getUserId(), getCourseId(), getModuleId());
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldCompleteModule() {
        Module module = course.getModule(getModuleId());
        mockCatalogueCall(course, module);
        ModuleResponse result = moduleService.completeModule(getUserId(), getCourseId(), getModuleId());
        assertEquals("Successfully applied action 'Complete a module' to course record", result.getMessage());
        assertEquals(getModuleId(), result.getModuleId());
        assertEquals("Test Module", result.getModuleTitle());
        assertEquals(getCourseId(), result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    private LaunchLink createLaunchLink() {
        return new LaunchLink("https://rustici-engine/RusticiEngine/defaultui/launch.jsp?jwt=eyJ0eXAiOiJKV1");
    }

    private CourseWithModule mockCatalogueCall(Course course, Module module) {
        CourseWithModule resp = new CourseWithModule(course, module);
        when(learningCatalogueService.getCourseWithModule(getCourseId(), getModuleId()))
                .thenReturn(resp);
        return resp;
    }


}
