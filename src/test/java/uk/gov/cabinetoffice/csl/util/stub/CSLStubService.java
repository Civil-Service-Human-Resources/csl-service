package uk.gov.cabinetoffice.csl.util.stub;

import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

@Service
@Getter
public class CSLStubService {

    @Autowired
    private LearnerRecordStubService learnerRecord;

    @Autowired
    private LearningCatalogueStubService learningCatalogue;

    @Autowired
    private RusticiStubService rustici;

    /**
     * Stub the following API calls:
     * - Get a course from the catalogue
     * - Get a blank course record from the learner record API
     * - Create a new course record
     *
     * @param courseId
     * @param course
     * @param userId
     * @param expectedCourseRecordInput
     * @param courseRecordResponse
     */
    public void stubCreateCourseRecord(String courseId, Course course, String userId,
                                       CourseRecordInput expectedCourseRecordInput, CourseRecord courseRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getCourseRecord(courseId, userId, null);
        learnerRecord.createCourseRecord(expectedCourseRecordInput, courseRecordResponse);
    }

    /**
     * Stub the following API calls:
     * - Get a course from the catalogue
     * - Get a course record from the learner record API
     * - Patch the module record
     * - Patch the course record (if required, pass null if not)
     *
     * @param courseId
     * @param course
     * @param userId
     * @param getCourseRecordsResponse
     * @param patchModuleRecordId
     * @param expectedModuleRecordPatches
     * @param patchModuleRecordResponse
     * @param expectedCourseRecordPatches
     * @param patchCourseRecordResponse
     */
    public void stubUpdateCourseRecord(String courseId, Course course, String userId, CourseRecords getCourseRecordsResponse,
                                       Integer patchModuleRecordId, List<PatchOp> expectedModuleRecordPatches, ModuleRecord patchModuleRecordResponse,
                                       @Nullable List<PatchOp> expectedCourseRecordPatches, @Nullable CourseRecord patchCourseRecordResponse) {
        learningCatalogue.getCourse(courseId, course);
        learnerRecord.getCourseRecord(courseId, userId, getCourseRecordsResponse);
        learnerRecord.patchModuleRecord(patchModuleRecordId, expectedModuleRecordPatches, patchModuleRecordResponse);
        if (patchCourseRecordResponse != null && expectedCourseRecordPatches != null) {
            learnerRecord.patchCourseRecord(expectedCourseRecordPatches, patchCourseRecordResponse);
        }
    }

}

