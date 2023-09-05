package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleLaunchServiceTest {

    @Mock
    private RusticiService rusticiService;

    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private ModuleLaunchService moduleLaunchService;
    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final String uid = "uid";
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";

    @BeforeEach
    public void setup() {
        String[] disabledBookmarkingModuleIDs = {"mockModuleID"};
        ReflectionTestUtils.setField(moduleLaunchService, "disabledBookmarkingModuleIDs", disabledBookmarkingModuleIDs);
        reset();
    }

    private void mockCreateLaunchLink(LaunchLink returnLaunchLink) {
        RegistrationInput registrationInput = createRegistrationInput();
        when(rusticiService.createLaunchLink(registrationInput)).thenReturn(returnLaunchLink);
    }

//    @Test
//    public void shouldGetLaunchLink() {
//        ModuleRecord moduleRecord = cslTestUtil.createModuleRecord();
//        moduleRecord.setUid("uid");
//        LaunchLink launchLink = createLaunchLink();
//        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
//        when(moduleService.launchModule(learnerId, courseId, moduleId, launchLinkInput))
//                .thenReturn(moduleRecord);
//        mockCreateLaunchLink(launchLink);
//        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, launchLinkInput);
//        assertEquals(launchLink, result);
//    }

//    @Test
//    public void shouldGetLaunchLinkAndAppendBookmarking() {
//        ModuleRecord moduleRecord = cslTestUtil.createModuleRecord();
//        moduleRecord.setUid("uid");
//        moduleRecord.setModuleId("mockModuleId");
//        LaunchLink launchLink = createLaunchLink();
//        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
//        when(moduleService.launchModule(learnerId, courseId, "mockModuleId", launchLinkInput))
//                .thenReturn(moduleRecord);
//        RegistrationInput registrationInput = createRegistrationInput();
//        registrationInput.setModuleId("mockModuleId");
//        when(rusticiService.createLaunchLink(registrationInput)).thenReturn(launchLink);
//        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, "mockModuleId", launchLinkInput);
//        assertEquals(launchLink, result);
//    }

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
