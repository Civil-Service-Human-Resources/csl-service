package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.cabinetoffice.csl.domain.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import static java.util.UUID.randomUUID;

@SpringBootTest
public class ModuleRollupServiceTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @InjectMocks
    private ModuleRollupService moduleRollupService;

    private CslTestUtil cslTestUtil;

    private final String learnerId = "learnerId";
    private final String courseId = "courseId";

    private final String moduleId = "moduleId";

    private final String uid = randomUUID().toString();

    private final String learnerFirstName = "learnerFirstName";
    private final String learnerLastName = "";

    @BeforeEach
    public void setup() {
        moduleRollupService = new ModuleRollupService(learnerRecordService);
        cslTestUtil = new CslTestUtil(learnerRecordService, learnerId, courseId, moduleId, uid);
    }

    private ModuleRecord invokeService() {
        return moduleRollupService.processRusticiRollupData(createRusticiRollupData());
    }

    private RusticiRollupData createRusticiRollupData() {
        RusticiRollupData rusticiRollupData = new RusticiRollupData();
        return rusticiRollupData;
    }
}
