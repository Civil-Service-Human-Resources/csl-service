package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.util.Cacheable;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course implements Serializable, Cacheable {
    private String id;
    private String title;
    private String shortDescription;
    private Collection<Module> modules = Collections.emptyList();
    private List<Audience> audiences = Collections.emptyList();

    private Map<String, Integer> departmentCodeToRequiredAudienceMap = new HashMap<>();
    private List<String> requiredModuleIdsForCompletion = new ArrayList<>();

    @JsonIgnore
    public Audience getRequiredAudienceWithDepCode(String departmentCode) {
        Audience audience = null;
        Integer index = departmentCodeToRequiredAudienceMap.get(departmentCode);
        if (index != null) {
            audience = audiences.get(index);
        }
        return audience;
    }

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
            Audience audience = getRequiredAudienceWithDepCode(code);
            if (audience != null) {
                LearningPeriod learningPeriod = audience.getLearningPeriod();
                if (learningPeriod != null) {
                    return Optional.of(learningPeriod);
                }
            }
        }
        return Optional.empty();
    }

    public Collection<Module> getRemainingModulesForCompletion(CourseRecord courseRecord, User user) {
        log.debug(String.format("Getting learning period for course %s and department codes %s", this.getId(), user.getDepartmentCodes()));
        LearningPeriod learningPeriod = getLearningPeriodForDepartmentHierarchy(user.getDepartmentCodes()).orElse(null);
        log.debug(String.format("Selected learning period: %s", learningPeriod));
        Map<String, State> realModuleStates = new HashMap<>();
        courseRecord.getModuleRecords().forEach(mr -> {
            State moduleRecordState = mr.getStateForLearningPeriod(learningPeriod);
            realModuleStates.put(mr.getModuleId(), moduleRecordState);
        });
        return getModulesRequiredForCompletion().stream().filter(m -> {
            State moduleState = realModuleStates.getOrDefault(m.getId(), State.NULL);
            return !moduleState.equals(State.COMPLETED);
        }).collect(Collectors.toSet());
    }

}
