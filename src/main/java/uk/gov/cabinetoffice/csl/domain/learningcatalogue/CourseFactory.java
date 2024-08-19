package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseFactory {

    private final LearningPeriodFactory learningPeriodFactory;

    public List<String> getRequiredModulesForCompletion(Collection<Module> modules) {
        List<String> optionalModuleIds = new ArrayList<>();
        List<String> nonOptionalModuleIds = new ArrayList<>();
        modules.forEach(m -> {
            String id = m.getId();
            if (m.isOptional()) {
                optionalModuleIds.add(id);
            } else {
                nonOptionalModuleIds.add(id);
            }
        });
        return nonOptionalModuleIds.size() > 0 ? nonOptionalModuleIds : optionalModuleIds;
    }

    public Map<String, Integer> buildRequiredLearningDepartmentMap(List<Audience> audiences) {
        Map<String, Integer> requiredLearningDepartmentMap = new HashMap<>();
        for (int i = 0; i < audiences.size(); i++) {
            Audience a = audiences.get(i);
            if (a.isRequiredForDepartments()) {
                LearningPeriod learningPeriod = learningPeriodFactory.buildLearningPeriod(a);
                a.setLearningPeriod(learningPeriod);
                for (String department : a.getDepartments()) {
                    requiredLearningDepartmentMap.put(department, i);
                }
            }
        }
        return requiredLearningDepartmentMap;
    }
}
