package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

@Service
public class ModuleRecordFactory {
    public ModuleRecord create(String userId, CourseWithModule courseWithModule) {
        Course course = courseWithModule.getCourse();
        Module module = courseWithModule.getModule();
        ModuleRecord moduleRecord = new ModuleRecord(course.getId(), course.getTitle(), userId, module.getId(), module.getTitle(), module.getModuleType(),
                module.getDuration(), module.isOptional(), module.getCost());
        moduleRecord.setNewRecord(true);
        return moduleRecord;
    }
}
