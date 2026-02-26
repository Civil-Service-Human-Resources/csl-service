package uk.gov.cabinetoffice.csl.service.skills;

import org.junit.jupiter.api.Test;
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

    Map<String, String> uidsToEmails = new HashMap<>();

    {
        for (int i = 0; i < 10; i++) {
            uidsToEmails.put("uid" + i, "email" + i + "@email.com");
        }
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
                testDataService.generateLearnerRecord("uid11", "course1", dummyDate, dummyDate)
        );
        SkillsLearnerRecordResponse response = factory.buildResponse(uidsToEmails, new LearnerRecordCollection(learnerRecords), 23);
        // 8 Records in the response
        assertEquals(8, response.getRecordCount());
        // 6 Users in the response
        assertEquals(6, response.getUserCount());
        // 13 Remaining users to be synced in total (23 total users - 10 users processed)
        assertEquals(12, response.getRemainingUsers());
        List<String> uids = new ArrayList<>(response.getUids());
        Collections.sort(uids);
        // List of the 10 UIDs that were processed in total
        assertEquals(List.of("uid0", "uid1", "uid11", "uid2", "uid3", "uid4", "uid5", "uid6", "uid7", "uid8", "uid9"), uids);
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
        SkillsLearnerRecordResponse response = factory.buildResponse(uidsToEmails, new LearnerRecordCollection(learnerRecords), 23);
        // 10 Records in the response
        assertEquals(10, response.getRecordCount());
        // 4 Users in the response
        assertEquals(3, response.getUserCount());
        // 15 Remaining users to be synced in total (19 total users - (4 users processed + 4 not found in learner records))
        assertEquals(16, response.getRemainingUsers());
        List<String> uids = new ArrayList<>(response.getUids());
        Collections.sort(uids);
        // List of the 8 UIDs (4 users processed + 4 not found in learner records) that were processed in total
        // uid0 and uid3 are in the learner records but were not processed due to the limit. So leave them
        // for the next process
        assertEquals(List.of("uid0", "uid1", "uid5", "uid6", "uid7", "uid8", "uid9"), uids);
    }
}
