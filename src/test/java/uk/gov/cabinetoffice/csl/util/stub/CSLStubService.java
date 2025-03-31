package uk.gov.cabinetoffice.csl.util.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
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

    public void stubCreateCourseRecord(String courseId, Course course, String userId,
                                       String expectedUpdateInput, CourseRecords courseRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getCourseRecord(courseId, userId, new CourseRecords());
        learnerRecord.createCourseRecord(expectedUpdateInput, courseRecordResponse);
    }

    public void stubCreateCourseRecord(String courseId, Course course, String userId,
                                       String expectedUpdateInput, CourseRecord courseRecordResponse) {
        stubCreateCourseRecord(courseId, course, userId, expectedUpdateInput, new CourseRecords(courseRecordResponse));
    }


    public void stubUpdateCourseRecord(String courseId, Course course, String userId, CourseRecords getCourseRecordsResponse,
                                       String expectedUpdateInput, CourseRecord updateCourseRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getCourseRecord(courseId, userId, getCourseRecordsResponse);
        learnerRecord.updateCourseRecords(expectedUpdateInput, new CourseRecords(List.of(updateCourseRecordResponse)));
    }

    public void stubUpdateCourseRecords(List<CourseRecordId> courseRecordIds, List<Course> courses, CourseRecords getCourseRecordsResponse,
                                        String expectedUpdateInput, CourseRecords updateCourseRecordResponse) {
        courses.forEach(c -> learningCatalogue.getCourse(c.getId(), c));
        learnerRecord.getCourseRecords(courseRecordIds, getCourseRecordsResponse);
        learnerRecord.updateCourseRecords(expectedUpdateInput, updateCourseRecordResponse);
    }

}

