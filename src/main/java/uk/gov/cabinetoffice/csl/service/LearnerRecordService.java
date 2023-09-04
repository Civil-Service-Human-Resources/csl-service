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
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.util.HashSet;
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

    private CourseWithModule getCourseWithModule(String courseId, String moduleId) {
        Course course = getCourse(courseId);
        Module module = course.getModule(moduleId);
        if (module != null) {
            return new CourseWithModule(course, module);
        } else {
            throw new LearningCatalogueResourceNotFoundException(String.format("Module '%s' in course '%s'", moduleId, courseId));
        }
    }

    public boolean isCourseCompleted(CourseRecord courseRecord) {
        String courseId = courseRecord.getCourseId();
        List<String> completedModuleIds = courseRecord.getModuleRecords()
                .stream()
                .filter(mr -> mr.getState().equals(State.COMPLETED))
                .map(ModuleRecord::getModuleId)
                .toList();
        Course course = getCourse(courseId);
        if (course.getModules() != null) {
            List<String> mandatoryModulesIds = course.getModules().stream()
                    .filter(m -> !m.isOptional()).map(Module::getId).toList();
            if (mandatoryModulesIds.size() > 0) {
                return new HashSet<>(completedModuleIds).containsAll(mandatoryModulesIds);
            } else {
                List<String> optionalModulesIds = course.getModules().stream()
                        .filter(Module::isOptional).map(Module::getId).toList();
                return new HashSet<>(completedModuleIds).containsAll(optionalModulesIds);
            }
        }
        return false;
    }

    @CacheEvict(value = "course-record", key = "{#learnerId, #courseId}")
    public void bustCourseRecordCache(String learnerId, String courseId) {

    }

    @CachePut(value = "course-record", key = "{ #courseRecord.getUserId(), #courseRecord.getCourseId() }")
    public CourseRecord updateCourseRecordCache(CourseRecord courseRecord) {
        return courseRecord;
    }

    @Cacheable(value = "course-record", key = "{#learnerId, #courseId}")
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
        CourseWithModule courseWithModule = getCourseWithModule(courseId, moduleId);
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
        CourseWithModule courseWithModule = getCourseWithModule(courseId, moduleId);
        ModuleRecordInput input = ModuleRecordInput.from(learnerId, courseId, courseWithModule.getModule(), moduleRecordStatus);
        return client.createModuleRecord(input);
    }

    public ModuleRecord updateModuleRecord(Long moduleRecordId, List<PatchOp> patches) {
        return client.updateModuleRecord(moduleRecordId, patches);
    }

}
