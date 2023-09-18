package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.Learner;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class RusticiCSLDataServiceTest {

    RusticiCSLDataService rusticiCSLDataService = new RusticiCSLDataService();

    @Test
    public void shouldGetCSLPropsFromRollup() {
        Course course = new Course();
        course.setId("courseId.moduleId");
        Learner learner = new Learner();
        learner.setId("learnerId");
        RusticiRollupData rollupData = new RusticiRollupData(
                "id", "", null, course,
                learner, LocalDateTime.now(), LocalDateTime.of(
                2023, 1, 1, 10, 0, 0)
        );
        CSLRusticiProps result = rusticiCSLDataService.getCSLDataFromRollUpData(rollupData);
        List<PatchOp> patches = result.getModuleRecordPatches();
        assertEquals(patches.get(0).getPath(), "/state");
        assertEquals(patches.get(0).getValue(), "COMPLETED");
        assertEquals(patches.get(1).getPath(), "/completionDate");
        assertEquals(patches.get(1).getValue(), "2023-01-01T10:00");
        assertEquals("courseId", result.getCourseId());
        assertEquals("moduleId", result.getModuleId());
        assertEquals("learnerId", result.getLearnerId());
    }
}
