package uk.gov.cabinetoffice.csl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CslTestUtil {
    private final String learnerId;
    private final String courseId;
    private final String moduleId;
    private final String uid;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime completedAt;

    private final String courseTitle = "courseTitle";
    private final Boolean isRequired = true;
    private final String moduleTitle = "moduleTitle";
    private final String moduleType = "elearning";
    private static ObjectMapper mapper = new ObjectMapper();

    public CslTestUtil(String learnerId, String courseId, String moduleId, String uid,
                       LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime completedAt) {
        this.learnerId = learnerId;
        this.courseId = courseId;
        this.moduleId = moduleId;
        this.uid = uid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public CourseRecord createCourseRecord() {
        return new CourseRecord(courseId, learnerId, courseTitle, State.IN_PROGRESS, null, null,
                null, isRequired, createModuleRecords(), updatedAt);
    }

    public List<ModuleRecord> createModuleRecords() {
        List<ModuleRecord> moduleRecords = new ArrayList<>();
        moduleRecords.add(createModuleRecord());
        return moduleRecords;
    }

    public ModuleRecord createModuleRecord() {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setId(1L);
        moduleRecord.setUid(uid);
        moduleRecord.setModuleId(moduleId);
        moduleRecord.setModuleTitle(moduleTitle);
        moduleRecord.setModuleType(moduleType);
        moduleRecord.setOptional(false);
        moduleRecord.setState(State.IN_PROGRESS);
        moduleRecord.setCreatedAt(createdAt);
        moduleRecord.setUpdatedAt(updatedAt);
        moduleRecord.setCompletionDate(completedAt);
        return moduleRecord;
    }

    @SneakyThrows
    public static String toJson(Object o) {
        return mapper.writeValueAsString(o);
    }

}
