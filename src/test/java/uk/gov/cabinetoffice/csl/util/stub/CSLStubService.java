package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.requestMadeFor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Service
@Getter
@RequiredArgsConstructor
public class CSLStubService {

    private final LearnerRecordStubService learnerRecord;
    private final LearningCatalogueStubService learningCatalogue;
    private final RusticiStubService rustici;
    private final ReportServiceStubService reportServiceStubService;
    private final CSRSStubService csrsStubService;
    private final NotificationServiceStubService notificationServiceStubService;
    private final FrontendStubService frontend;
    private final IdentityAPIServiceStubService identityAPIServiceStubService;

    public void assertStubbedRequests(List<StubMapping> stubs) {
        stubs.forEach(stub -> assertEquals(1, WireMock.findAll(requestMadeFor(stub.getRequest())).size(),
                String.format("Expected endpoint %s to have been called once", stub.getRequest().getExpected())));
    }

    public StubMapping stubGetUserDetails(String uid, CivilServant civilServant) {
        return getCsrsStubService().getCivilServant(uid, civilServant);
    }

    public StubMapping stubGetOrganisations(OrganisationalUnitsPagedResponse organisationalUnitsPagedResponse) {
        return getCsrsStubService().getOrganisations(organisationalUnitsPagedResponse);
    }

    public StubMapping stubDeleteOrganisationalUnit(Long organisationalUnitId) {
        return getCsrsStubService().deleteOrganisationalUnit(organisationalUnitId);
    }

    public List<StubMapping> stubCreateModuleRecords(String courseId, String moduleId, Course course, String userId,
                                                     String expectedUpdateInput, String moduleRecordResponse) {
        return List.of(
                learningCatalogue.getCourse(courseId, course),
                learnerRecord.getModuleRecord(moduleId, userId, """
                        {"moduleRecords": []}
                        """),
                learnerRecord.createModuleRecords(expectedUpdateInput, moduleRecordResponse));
    }

    public void stubUpdateModuleRecord(Course course, String moduleId, String userId, String getModuleRecordsResponse,
                                       String expectedUpdateInput, String updateModuleRecordResponse) {
        learningCatalogue.getCourse(course);
        learnerRecord.getModuleRecord(moduleId, userId, getModuleRecordsResponse);
        learnerRecord.updateModuleRecords(expectedUpdateInput, updateModuleRecordResponse);
    }

    public void stubUpdateModuleRecords(List<LearnerRecordResourceId> moduleRecordIds, List<Course> courses, String getModuleRecordsResponse,
                                        String expectedUpdateInput, String updateModuleRecordResponse) {
        courses.forEach(c -> learningCatalogue.getCourse(c.getCacheableId(), c));
        learnerRecord.getModuleRecords(moduleRecordIds, getModuleRecordsResponse);
        learnerRecord.updateModuleRecords(expectedUpdateInput, updateModuleRecordResponse);
    }

}

