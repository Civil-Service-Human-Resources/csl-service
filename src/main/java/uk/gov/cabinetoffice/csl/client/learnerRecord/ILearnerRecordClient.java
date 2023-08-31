package uk.gov.cabinetoffice.csl.client.learnerRecord;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;

import java.util.List;

public interface ILearnerRecordClient {

    CourseRecords getCourseRecord(String userId, String courseId);

    CourseRecords getCourseRecords(String userId, List<String> courseId);

    CourseRecord createCourseRecord(CourseRecordInput body);

    ModuleRecord createModuleRecord(ModuleRecordInput body);

    CourseRecord updateCourseRecord(String userId, String courseId, List<PatchOp> patches);

    ModuleRecord updateModuleRecord(Long moduleRecordId, List<PatchOp> patches);
}