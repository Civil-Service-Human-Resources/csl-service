package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class CourseWithRecord implements ILearningResource {

    private final String learnerId;
    private final String courseId;
    private final String courseTitle;
    private final Collection<Module> modules;
    @Nullable
    private final LearnerRecord record;

    public LearnerRecordEvent getLatestEvent() {
        return record == null ? null : record.getLatestEvent();
    }

    @Override
    public String getResourceId() {
        return courseId;
    }

    @Override
    public String getName() {
        return courseTitle;
    }

    @Override
    public LearningResourceType getType() {
        return LearningResourceType.COURSE;
    }

    public Collection<ModuleRecordResourceId> getModuleResourceIds() {
        return modules.stream().map(m -> new ModuleRecordResourceId(learnerId, m.getId())).toList();
    }
}
