package uk.gov.cabinetoffice.csl.service.skills;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServantSkillsMetadataCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordCollection;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordResponse;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillsLearnerRecordFactoryTest {

    private final TestDataService testDataService = new TestDataService();

    private final SkillsLearnerRecordFactory factory = new SkillsLearnerRecordFactory(10);

    private final LocalDateTime dummyDate = LocalDateTime.of(2025, 1, 1, 10, 0);

    private final List<String> uids = new ArrayList<>();

    {
        for (int i = 0; i < 11; i++) {
            uids.add("uid" + i);
        }
    }

    Map<String, String> uidsToEmails = new HashMap<>();

    {
        uids.forEach(uid -> uidsToEmails.put(uid, "email" + uid + "@email.com"));
    }

    @Test
    void shouldBuildResponseWithLearnerRecords() {
        List<LearnerRecord> learnerRecords = List.of(
                testDataService.generateLearnerRecord("uid0", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid0", "course2", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid2", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid3", "course2", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid4", "course3", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid4", "course4", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid5", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid10", "course1", dummyDate, dummyDate)
        );
        CivilServantSkillsMetadataCollection civilServantSkillsMetadataCollection = new CivilServantSkillsMetadataCollection(uids, dummyDate, 23);
        SkillsLearnerRecordResponse response = factory.buildResponse(civilServantSkillsMetadataCollection, uidsToEmails, new LearnerRecordCollection(learnerRecords));
        // 9 Records in the response
        assertEquals(9, response.getRecordCount());
        // 6 Users in the response
        assertEquals(7, response.getUserCount());
        // 12 Remaining users to be synced in total (23 total users - 11 users processed)
        assertEquals(12, response.getRemainingUsers());
        List<String> uids = new ArrayList<>(response.getUids());
        Collections.sort(uids);
        assertEquals(List.of("uid0", "uid1", "uid10", "uid2", "uid3", "uid4", "uid5", "uid6", "uid7", "uid8", "uid9"), uids);
    }

    @Test
    void shouldBuildResponseWithLearnerRecordsTooManyRecords() {
        List<LearnerRecord> learnerRecords = List.of(
                testDataService.generateLearnerRecord("uid0", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid0", "course2", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid2", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid3", "course2", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid4", "course3", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid0", "course3", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid4", "course4", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid5", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course1", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course2", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course3", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course4", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course5", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid6", "course6", dummyDate, dummyDate)
        );
        CivilServantSkillsMetadataCollection civilServantSkillsMetadataCollection = new CivilServantSkillsMetadataCollection(uids, dummyDate, 23);
        SkillsLearnerRecordResponse response = factory.buildResponse(civilServantSkillsMetadataCollection, uidsToEmails, new LearnerRecordCollection(learnerRecords));
        assertEquals(10, response.getRecordCount());
        assertEquals(3, response.getUserCount());
        assertEquals(12, response.getRemainingUsers());
        List<String> uids = new ArrayList<>(response.getUids());
        Collections.sort(uids);
        assertEquals(List.of("uid0", "uid1", "uid10", "uid2", "uid3", "uid4", "uid5", "uid6", "uid7", "uid8", "uid9"), uids);
    }

    @Test
    public void testEmptyFields() {
        CivilServantSkillsMetadataCollection civilServantSkillsMetadataCollection = new CivilServantSkillsMetadataCollection(uids, dummyDate, 320);
        SkillsLearnerRecordResponse response = factory.buildResponse(civilServantSkillsMetadataCollection, new HashMap<>(), new LearnerRecordCollection());
        assertEquals(0, response.getRecordCount());
        assertEquals(0, response.getUserCount());
        assertEquals(309, response.getRemainingUsers());
        List<String> uids = new ArrayList<>(response.getUids());
        Collections.sort(uids);
        assertEquals(List.of("uid0", "uid1", "uid10", "uid2", "uid3", "uid4", "uid5", "uid6", "uid7", "uid8", "uid9"), uids);
    }
}
