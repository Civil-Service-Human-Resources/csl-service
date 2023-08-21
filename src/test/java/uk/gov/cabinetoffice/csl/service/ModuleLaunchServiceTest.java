package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cabinetoffice.csl.configuration.MockClockConfig;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.rustici.LaunchLink;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.domain.rustici.RegistrationInput;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;
import uk.gov.cabinetoffice.csl.util.StringUtilService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class ModuleLaunchServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private RusticiService rusticiService;

    @Mock
    private StringUtilService stringUtilService;

    @InjectMocks
    private ModuleLaunchService moduleLaunchService;

    private CslTestUtil cslTestUtil;
    private final String learnerId = "learnerId";
    private final String courseId = "courseId";
    private final String moduleId = "moduleId";
    private final long moduleRecordId = 1;
    private final String uid = "uid";
    private final LocalDateTime currentDateTime = LocalDateTime.now();
    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";
    CourseRecordStatus expectedCourseRecordStatus = CourseRecordStatus.builder().state("IN_PROGRESS").build();
    ModuleRecordStatus expectedModuleRecordStatus = ModuleRecordStatus.builder()
            .state("IN_PROGRESS").uid("uid").build();

    @BeforeEach
    public void setup() {
        cslTestUtil = new CslTestUtil(learnerId, courseId, moduleId, uid,
                currentDateTime, currentDateTime, currentDateTime);
        when(stringUtilService.generateRandomUuid()).thenReturn(uid);
        Clock clock = new MockClockConfig().getMockClock();
        ReflectionTestUtils.setField(moduleLaunchService, "clock", clock);

        String[] disabledBookmarkingModuleIDs = {"mockModuleID"};
        ReflectionTestUtils.setField(moduleLaunchService, "disabledBookmarkingModuleIDs", disabledBookmarkingModuleIDs);
        reset();
    }

    private void mockGetCourseRecord(CourseRecord returnCourseRecord) {
        when(learnerRecordService.getCourseRecord(learnerId, courseId)).thenReturn(returnCourseRecord);
    }

    private void mockCreateModuleRecord(ModuleRecordStatus expectedModuleRecordStatus, ModuleRecord returnModuleRecord) {
        when(learnerRecordService.createModuleRecord(learnerId, courseId, moduleId, expectedModuleRecordStatus))
                .thenReturn(returnModuleRecord);
    }

    private void mockCreateCourseRecord(CourseRecordStatus expectedCourseRecordStatus, ModuleRecordStatus expectedModuleRecordStatus,
                                        CourseRecord returnCourseRecord) {
        when(learnerRecordService.createCourseRecord(learnerId, courseId, moduleId, expectedCourseRecordStatus, expectedModuleRecordStatus))
                .thenReturn(returnCourseRecord);
    }

    private void mockUpdateCourseRecord(List<PatchOp> expectedPatches, CourseRecord returnCourseRecord) {
        when(learnerRecordService.updateCourseRecord(learnerId, courseId, expectedPatches)).thenReturn(returnCourseRecord);
    }

    private void mockUpdateModuleRecord(List<PatchOp> expectedPatches, ModuleRecord returnModuleRecord) {
        when(learnerRecordService.updateModuleRecord(moduleRecordId, expectedPatches)).thenReturn(returnModuleRecord);
    }

    private void mockCreateLaunchLink(LaunchLink returnLaunchLink) {
        RegistrationInput registrationInput = createRegistrationInput();
        when(rusticiService.createLaunchLink(registrationInput)).thenReturn(returnLaunchLink);
    }

    @Test
    public void shouldCreateCourseRecordAndGetLaunchLink() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        LaunchLink launchLink = createLaunchLink();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        mockGetCourseRecord(null);
        expectedCourseRecordStatus.setIsRequired(true);
        mockCreateCourseRecord(expectedCourseRecordStatus, expectedModuleRecordStatus, courseRecord);
        mockCreateLaunchLink(launchLink);
        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldUpdateCourseRecordWhenArchivedAndGetLaunchLink() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        courseRecord.setState(null);
        LaunchLink launchLink = createLaunchLink();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        List<PatchOp> expectedCourseRecordPatches = List.of(PatchOp.replacePatch("state", "IN_PROGRESS"));
        mockGetCourseRecord(courseRecord);
        mockUpdateCourseRecord(expectedCourseRecordPatches, courseRecord);
        mockCreateLaunchLink(launchLink);
        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldNotUpdateCourseRecordWhenArchivedAndGetLaunchLink() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        LaunchLink launchLink = createLaunchLink();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        List<PatchOp> expectedCourseRecordPatches = List.of(PatchOp.replacePatch("state", "IN_PROGRESS"));
        courseRecord.setState(State.ARCHIVED);
        mockGetCourseRecord(courseRecord);
        mockUpdateCourseRecord(expectedCourseRecordPatches, courseRecord);
        mockCreateLaunchLink(launchLink);
        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldCreateModuleRecordAndGetLaunchLink() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        courseRecord.setModuleRecords(Collections.emptyList());
        ModuleRecord moduleRecord = cslTestUtil.createModuleRecord();
        LaunchLink launchLink = createLaunchLink();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        mockGetCourseRecord(courseRecord);
        mockCreateModuleRecord(expectedModuleRecordStatus, moduleRecord);
        mockCreateLaunchLink(launchLink);
        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldUpdateModuleRecordAndGetLaunchLink() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        courseRecord.getModuleRecord(moduleId).setUid("");
        LaunchLink launchLink = createLaunchLink();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        mockGetCourseRecord(courseRecord);
        List<PatchOp> expectedModuleRecordPatches = List.of(
                PatchOp.replacePatch("updatedAt", "2023-01-01T10:00"),
                PatchOp.replacePatch("uid", "uid"));
        mockUpdateModuleRecord(expectedModuleRecordPatches, courseRecord.getModuleRecord(moduleId));
        mockCreateLaunchLink(launchLink);
        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, moduleId, launchLinkInput);
        assertEquals(launchLink, result);
    }

    @Test
    public void shouldNotUpdateModuleRecordAndGetLaunchLinkAndAppendBookmarking() {
        CourseRecord courseRecord = cslTestUtil.createCourseRecord();
        courseRecord.getModuleRecord(moduleId).setModuleId("mockModuleID");
        LaunchLink launchLink = createLaunchLink();
        launchLink.clearBookmarking();
        ModuleLaunchLinkInput launchLinkInput = createModuleLaunchLinkInput();
        mockGetCourseRecord(courseRecord);
        RegistrationInput reg = new RegistrationInput(uid, courseId, "mockModuleID", learnerId, learnerFirstName, learnerLastName);
        when(rusticiService.createLaunchLink(reg)).thenReturn(launchLink);
        LaunchLink result = moduleLaunchService.createLaunchLink(learnerId, courseId, "mockModuleID", launchLinkInput);
        assertEquals(launchLink, result);
        verify(learnerRecordService, never()).updateCourseRecord(any(), any(), any());
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
