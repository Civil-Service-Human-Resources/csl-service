package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course implements Serializable {
    private String id;
    private String title;
    private Collection<Module> modules;
    private Collection<Audience> audiences;

    @JsonIgnore
    public Module getModule(String moduleId) {
        List<Module> modules = this.modules.stream().filter(m -> m.getId().equals(moduleId)).toList();
        if (modules.size() != 1) {
            return null;
        } else {
            return modules.get(0);
        }
    }

    @JsonIgnore
    public boolean isCourseComplete(CourseRecord courseRecord) {
        List<String> completedModuleIds = courseRecord.getModuleRecords()
                .stream()
                .filter(mr -> mr.getStateSafe().equals(State.COMPLETED))
                .map(ModuleRecord::getModuleId)
                .toList();
        if (getModules() != null) {
            List<String> mandatoryModulesIds = getModules().stream()
                    .filter(m -> !m.isOptional()).map(Module::getId).toList();
            if (mandatoryModulesIds.size() > 0) {
                return new HashSet<>(completedModuleIds).containsAll(mandatoryModulesIds);
            } else {
                List<String> optionalModulesIds = getModules().stream()
                        .filter(Module::isOptional).map(Module::getId).toList();
                return new HashSet<>(completedModuleIds).containsAll(optionalModulesIds);
            }
        }
        return false;
    }
}
