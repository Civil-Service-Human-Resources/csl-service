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
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.ModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleServiceTest extends TestDataService {

    @Mock
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    @Mock
    private LearningCatalogueService learningCatalogueService;

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
        UserDetailsDto userDetailsDto = new UserDetailsDto();
        LaunchLink result = moduleService.launchModule(user, getCourseId(), getModuleId(), userDetailsDto);
        verify(learnerRecordUpdateProcessor, atMostOnce()).processModuleRecordAction(courseWithModule, user, ModuleRecordAction.LAUNCH_MODULE, null);
        assertEquals("https://test.com", result.getLaunchLink());
    }

    @Test
    public void shouldLaunchELearning() {
        Module module = course.getModule(getModuleId());
        module.setModuleType(ModuleType.elearning);
        CourseRecord courseRecord = generateCourseRecord(true);
        courseRecord.getModuleRecord(getModuleId()).get().setUid("uid");
        CourseWithModule courseWithModule = mockCatalogueCall(course, module);
        when(learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.LAUNCH_MODULE, null))
                .thenReturn(courseRecord);
        LaunchLink launchLink = createLaunchLink();
        UserDetailsDto userDetailsDto = generateUserDetailsDto();
        RegistrationInput registrationInput = new RegistrationInput("uid", getCourseId(), getModuleId(), user.getId(), getLearnerFirstName(), "");
        when(rusticiService.createLaunchLink(registrationInput)).thenReturn(launchLink);
        LaunchLink result = moduleService.launchModule(user, getCourseId(), getModuleId(), userDetailsDto);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldCompleteModule() {
        Module module = course.getModule(getModuleId());
        CourseRecord courseRecord = generateCourseRecord(true);
        CourseWithModule courseWithModule = mockCatalogueCall(course, module);
        when(learnerRecordUpdateProcessor.processModuleRecordAction(courseWithModule, user, ModuleRecordAction.COMPLETE_MODULE, null))
                .thenReturn(courseRecord);
        ModuleResponse result = moduleService.completeModule(user, getCourseId(), getModuleId(), null);
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
