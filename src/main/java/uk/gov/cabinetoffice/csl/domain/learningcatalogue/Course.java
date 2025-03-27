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
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
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

    @JsonIgnore
    public void updateEvent(String moduleId, Event event) {
        Optional.ofNullable(getModule(moduleId))
                .ifPresent(module -> module.updateEvent(event));
    }

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
    public Optional<Audience> getAudienceForDepartmentHierarchy(Collection<String> departmentCodeHierarchy) {
        String[] departmentCodeHierarchyArray = departmentCodeHierarchy.toArray(new String[]{});
        for (int i = (departmentCodeHierarchyArray.length - 1); i >= 0; i--) {
            String code = departmentCodeHierarchyArray[i];
            Audience audience = getRequiredAudienceWithDepCode(code);
            if (audience != null) {
                return Optional.of(audience);
            }
        }
        return Optional.empty();
    }

    @JsonIgnore
    public Optional<LearningPeriod> getLearningPeriodForDepartmentHierarchy(Collection<String> departmentCodeHierarchy) {
        Optional<Audience> optionalAudience = getAudienceForDepartmentHierarchy(departmentCodeHierarchy);
        return optionalAudience.map(Audience::getLearningPeriod);
    }

    @JsonIgnore
    public List<Module> getRequiredModulesForCompletion() {
        return this.modules.stream()
                .filter(Module::isRequiredForCompletion)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Collection<String> getRemainingModuleIdsForCompletion(CourseRecord courseRecord, User user) {
        log.debug(String.format("Getting learning period for course %s and department codes %s", this.getId(), user.getDepartmentCodes()));
        LearningPeriod learningPeriod = getLearningPeriodForDepartmentHierarchy(user.getDepartmentCodes()).orElse(null);
        log.debug(String.format("Selected learning period: %s", learningPeriod));
        Map<String, State> realModuleStates = new HashMap<>();
        courseRecord.getModuleRecords().forEach(mr -> {
            State moduleRecordState = mr.getStateForLearningPeriod(learningPeriod);
            realModuleStates.put(mr.getModuleId(), moduleRecordState);
        });
        return getRequiredModulesForCompletion().stream().filter(mod -> {
            State moduleState = realModuleStates.getOrDefault(mod.getId(), State.NULL);
            return !moduleState.equals(State.COMPLETED);
        }).map(Module::getId).collect(Collectors.toSet());
    }

}
