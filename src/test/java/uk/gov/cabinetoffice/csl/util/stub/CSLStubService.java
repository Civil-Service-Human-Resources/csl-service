package uk.gov.cabinetoffice.csl.util.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Service
@Getter
@RequiredArgsConstructor
public class CSLStubService {

    private final LearnerRecordStubService learnerRecord;
    private final LearningCatalogueStubService learningCatalogue;
    private final RusticiStubService rustici;
    private final ReportServiceStubService reportServiceStubService;
    private final CSRSStubService csrsStubService;

    public void stubGetUserDetails(String uid, CivilServant civilServant) {
        getCsrsStubService().getCivilServant(uid, civilServant);
    }

    public void stubCreateCourseRecord(String courseId, Course course, String userId,
                                       String expectedUpdateInput, CourseRecord courseRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getCourseRecord(courseId, userId, new CourseRecords());
        learnerRecord.createCourseRecord(expectedUpdateInput, courseRecordResponse);
    }


    public void stubUpdateCourseRecord(String courseId, Course course, String userId, CourseRecords getCourseRecordsResponse,
                                       String expectedUpdateInput, CourseRecord updateCourseRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getCourseRecord(courseId, userId, getCourseRecordsResponse);
        learnerRecord.updateCourseRecord(expectedUpdateInput, updateCourseRecordResponse);
    }

}

