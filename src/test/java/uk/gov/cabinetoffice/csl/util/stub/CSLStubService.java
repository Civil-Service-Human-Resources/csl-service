package uk.gov.cabinetoffice.csl.util.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.LearnerRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

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

    public void stubGetUserDetails(String uid, CivilServant civilServant) {
        getCsrsStubService().getCivilServant(uid, civilServant);
    }

    public void stubCreateModuleRecords(String courseId, String moduleId, Course course, String userId,
                                        String expectedUpdateInput, String moduleRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getModuleRecord(moduleId, userId, """
                {"moduleRecords": []}
                """);
        learnerRecord.createModuleRecords(expectedUpdateInput, moduleRecordResponse);
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

