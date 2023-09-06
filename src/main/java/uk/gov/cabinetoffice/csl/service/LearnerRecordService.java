package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.domain.error.LearningCatalogueResourceNotFoundException;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;

import java.util.List;

@Slf4j
@Service
public class LearnerRecordService {

    private final ILearnerRecordClient client;
    private final LearningCatalogueService learningCatalogueService;

    public LearnerRecordService(ILearnerRecordClient client, LearningCatalogueService learningCatalogueService) {
        this.client = client;
        this.learningCatalogueService = learningCatalogueService;
    }

    private Course getCourse(String courseId) {
        Course course = learningCatalogueService.getCourse(courseId);
        if (course != null) {
            return course;
        } else {
            throw new LearningCatalogueResourceNotFoundException(String.format("Course '%s'", courseId));
        }
    }

    @CacheEvict(value = "course-record", key = "{#learnerId, #courseId}")
    public void bustCourseRecordCache(String learnerId, String courseId) {

    }

    @CachePut(value = "course-record", key = "{ #courseRecord.getUserId(), #courseRecord.getCourseId() }")
    public CourseRecord updateCourseRecordCache(CourseRecord courseRecord) {
        return courseRecord;
    }

    @Cacheable(value = "course-record", key = "{#learnerId, #courseId}", unless = "#result == null")
    public CourseRecord getCourseRecord(String learnerId, String courseId) {
        CourseRecords courseRecords = client.getCourseRecord(learnerId, courseId);
        if (courseRecords == null) {
            return null;
        }
        return courseRecords.getCourseRecord(courseId);
    }

    public CourseRecord createCourseRecord(String learnerId,
                                           String courseId,
                                           String moduleId,
                                           CourseRecordStatus courseRecordStatus,
                                           ModuleRecordStatus moduleRecordStatus) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        CourseRecordInput input = CourseRecordInput.from(learnerId, courseWithModule.getCourse(),
                courseRecordStatus, courseWithModule.getModule(), moduleRecordStatus);
        return client.createCourseRecord(input);
    }

    public CourseRecord createCourseRecord(String learnerId, String courseId, CourseRecordStatus courseRecordStatus) {
        Course course = getCourse(courseId);
        CourseRecordInput input = CourseRecordInput.from(learnerId, course, courseRecordStatus);
        return client.createCourseRecord(input);
    }

    public CourseRecord updateCourseRecord(String learnerId, String courseId, List<PatchOp> patches) {
        return client.updateCourseRecord(learnerId, courseId, patches);
    }

    public ModuleRecord createModuleRecord(String learnerId,
                                           String courseId,
                                           String moduleId,
                                           ModuleRecordStatus moduleRecordStatus) {
        CourseWithModule courseWithModule = learningCatalogueService.getCourseWithModule(courseId, moduleId);
        ModuleRecordInput input = ModuleRecordInput.from(learnerId, courseId, courseWithModule.getModule(), moduleRecordStatus);
        return client.createModuleRecord(input);
    }

    public ModuleRecord updateModuleRecord(Long moduleRecordId, List<PatchOp> patches) {
        return client.updateModuleRecord(moduleRecordId, patches);
    }

}
