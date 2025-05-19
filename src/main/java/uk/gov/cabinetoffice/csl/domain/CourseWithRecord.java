package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class CourseWithRecord extends Course implements ILearningResourceWithRecord {

    private final String learnerId;
    private final LearnerRecord record;

    public CourseWithRecord(String id, String title, String shortDescription, Collection<Module> modules,
                            List<Audience> audiences, Map<String, Integer> departmentCodeToRequiredAudienceMap,
                            String learnerId, LearnerRecord record) {
        super(id, title, shortDescription, modules, audiences, departmentCodeToRequiredAudienceMap);
        this.learnerId = learnerId;
        this.record = record;
    }

    public List<ModuleRecordResourceId> getModuleResourceIds() {
        return this.getModules().stream().map(m -> new ModuleRecordResourceId(this.learnerId, m.getResourceId())).toList();
    }

}
