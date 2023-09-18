package uk.gov.cabinetoffice.csl.integration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.util.List;

import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.getCourseRecord;
import static uk.gov.cabinetoffice.csl.util.stub.LearnerRecordStubService.patchModuleRecord;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class RusticiRollUpTest extends CSLServiceWireMockServer {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataService testDataService;

    CourseRecord courseRecord;
    CourseRecords courseRecords;
    RusticiRollupData rollupData;

    @PostConstruct
    public void setupData() {
        courseRecord = testDataService.generateCourseRecord(true);
        courseRecords = new CourseRecords(List.of(courseRecord));
        rollupData = testDataService.generateRusticiRollupData();
    }


    @Test
    public void testRollUpCompletedModuleRecord() {
        getCourseRecord(testDataService.getCourseId(),
                testDataService.getUserId(), courseRecords);
        patchModuleRecord(1, List.of(
                PatchOp.replacePatch("state", "COMPLETED"),
                PatchOp.replacePatch("completionDate", "2023-02-02T10:00")
        ), courseRecord.getModuleRecords().stream().findFirst().get());

        webTestClient
                .post()
                .uri("/rustici/rollup")
                .body(Mono.just(rollupData), RusticiRollupData.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

}
