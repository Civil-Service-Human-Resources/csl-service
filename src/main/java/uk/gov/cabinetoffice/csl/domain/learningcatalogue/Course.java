package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course implements Serializable {
    private String id;
    private String title;
    private Collection<Module> modules = Collections.emptyList();
    private Collection<Audience> audiences = Collections.emptyList();

    private Map<String, LearningPeriod> departmentDeadlineMap = new HashMap<>();

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
    public boolean isMandatoryLearningForUser(User user) {
        return getLearningPeriodForDepartmentHierarchy(user.getDepartmentCodes()).isPresent();
    }

    @JsonIgnore
    public Collection<Module> getModulesRequiredForCompletion() {
        Collection<Module> optionalModules = new ArrayList<>();
        Collection<Module> requiredModules = new ArrayList<>();
        getModules().forEach(m -> {
            if (m.isOptional()) {
                optionalModules.add(m);
            } else {
                requiredModules.add(m);
            }
        });
        if (requiredModules.isEmpty()) {
            return optionalModules;
        } else {
            return requiredModules;
        }
    }

    @JsonIgnore
    public Optional<LearningPeriod> getLearningPeriodForDepartmentHierarchy(Collection<String> departmentCodeHierarchy) {
        String[] departmentCodeHierarchyArray = departmentCodeHierarchy.toArray(new String[]{});
        for (int i = (departmentCodeHierarchyArray.length - 1); i >= 0; i--) {
            String code = departmentCodeHierarchyArray[i];
            LearningPeriod learningPeriod = departmentDeadlineMap.get(code);
            if (learningPeriod != null) {
                return Optional.of(learningPeriod);
            }
        }
        return Optional.empty();
    }

    public Collection<Module> getRemainingModulesForCompletion(CourseRecord courseRecord, User user) {
        Map<String, LocalDateTime> completedModuleDates = new HashMap<>();
        courseRecord.getModuleRecords().forEach(mr -> {
            if (mr.getState().equals(State.COMPLETED) && mr.getCompletionDate() != null) {
                completedModuleDates.put(mr.getModuleId(), mr.getCompletionDate());
            }
        });
        LearningPeriod learningPeriod = getLearningPeriodForDepartmentHierarchy(user.getDepartmentCodes()).orElse(null);
        return getModulesRequiredForCompletion().stream().filter(m -> {
            LocalDateTime completionDate = completedModuleDates.get(m.getId());
            return completionDate == null || (learningPeriod != null && !learningPeriod.isDateWithinPeriod(completionDate));
        }).collect(Collectors.toSet());
    }

}
