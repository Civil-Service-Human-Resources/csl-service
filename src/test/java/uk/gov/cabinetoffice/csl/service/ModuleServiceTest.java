package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.controller.model.ModuleResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.IModuleRecordUpdate;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.LearnerRecordUpdateProcessor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ModuleRecordUpdateService;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.ModuleType;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleServiceTest {

    @Mock
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;

    @Mock
    private LearningCatalogueService learningCatalogueService;

    @Mock
    private RusticiService rusticiService;

    @Mock
    private ModuleRecordUpdateService moduleRecordUpdateService;

    @InjectMocks
    private ModuleService moduleService;

    private TestDataService testDataService;
    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final String uid = "uid";
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";
    private final IModuleRecordUpdate update = mock(IModuleRecordUpdate.class);

    @BeforeEach
    public void setup() {
        testDataService = new TestDataService();
        reset();
    }

    @Test
    public void shouldLaunchModule() {
        Course course = testDataService.generateCourse(true);
        Module module = course.getModule(moduleId);
        module.setModuleType(ModuleType.link);
        module.setUrl("https://test.com");
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(moduleRecordUpdateService.getLaunchModuleUpdate(course, module, true))
                .thenReturn(update);
        when(learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update))
                .thenReturn(courseRecord);
        when(learningCatalogueService.getCourseWithModule(courseId, moduleId)).thenReturn(
                new CourseWithModule(course, module)
        );
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        launchLinkInput.setCourseIsRequired(true);
        LaunchLink result = moduleService.launchModule(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals("https://test.com", result.getLaunchLink());
    }

    @Test
    public void shouldLaunchELearning() {
        Course course = testDataService.generateCourse(true);
        Module module = course.getModule(moduleId);
        module.setModuleType(ModuleType.elearning);
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        courseRecord.getModuleRecord(moduleId).setUid("uid");
        when(moduleRecordUpdateService.getLaunchModuleUpdate(course, module, true))
                .thenReturn(update);
        when(learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update))
                .thenReturn(courseRecord);
        when(learningCatalogueService.getCourseWithModule(courseId, moduleId)).thenReturn(
                new CourseWithModule(course, module)
        );
        LaunchLink launchLink = createLaunchLink();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        RegistrationInput registrationInput = createRegistrationInput();
        when(rusticiService.createLaunchLink(registrationInput)).thenReturn(launchLink);
        LaunchLink result = moduleService.launchModule(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldCompleteModule() {
        Course course = testDataService.generateCourse(true);
        Module module = course.getModule(moduleId);
        CourseRecord courseRecord = testDataService.generateCourseRecord(true);
        when(learningCatalogueService.getCourseWithModule(courseId, moduleId)).thenReturn(
                new CourseWithModule(course, module)
        );
        when(moduleRecordUpdateService.getCompleteModuleUpdate(course, module))
                .thenReturn(update);
        when(learnerRecordUpdateProcessor.processModuleRecordAction(learnerId, courseId, moduleId, update))
                .thenReturn(courseRecord);
        ModuleResponse result = moduleService.completeModule(learnerId, courseId, moduleId);
        assertEquals("Module was successfully completed", result.getMessage());
        assertEquals(moduleId, result.getModuleId());
        assertEquals("Test Module", result.getModuleTitle());
        assertEquals(courseId, result.getCourseId());
        assertEquals("Test Course", result.getCourseTitle());
    }

    private ModuleLaunchLinkInput createModuleLaunchLinkInput() {
        return new ModuleLaunchLinkInput(learnerFirstName, learnerLastName, true);
    }

    private RegistrationInput createRegistrationInput() {
        return new RegistrationInput(uid, courseId, moduleId, learnerId, learnerFirstName, learnerLastName);
    }

    private LaunchLink createLaunchLink() {
        return new LaunchLink("https://rustici-engine/RusticiEngine/defaultui/launch.jsp?jwt=eyJ0eXAiOiJKV1");
    }


}