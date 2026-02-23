package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LearnerRecordCollectionTest {

    private final TestDataService testDataService = new TestDataService();
    private final LocalDateTime dummyDate = LocalDateTime.of(2025, 1, 1, 10, 0);


    @Test
    void testGetOrderedMapByUser() {
        LearnerRecordCollection learnerRecords = new LearnerRecordCollection(List.of(
                testDataService.generateLearnerRecord("uid0", "course1", dummyDate, null),
                testDataService.generateLearnerRecord("uid4", "course4", dummyDate, null),
                testDataService.generateLearnerRecord("uid0", "course2", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid4", "course3", dummyDate, dummyDate),
                testDataService.generateLearnerRecord("uid0", "course1", dummyDate, dummyDate)
        ));
        Map<String, Collection<LearnerRecord>> map = learnerRecords.getOrderedMapByUser(LearnerRecordCollection.COMPARATOR_NUMBER_OF_RECORDS_DESC);

        assertEquals(3, map.get("uid0").size());
        assertEquals(2, map.get("uid4").size());

        ArrayList<String> keys = new ArrayList<>(map.keySet());

        assertEquals("uid0", keys.get(0));
        assertEquals("uid4", keys.get(1));

    }
}
