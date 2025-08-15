package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequiredLearningMap implements Serializable {

    private Map<String, ArrayList<String>> departmentCodeMap;

    public Set<String> getRequiredLearningWithDepartmentCodes(Collection<String> depCodes) {
        return depCodes.stream().flatMap(code -> departmentCodeMap.getOrDefault(code, new ArrayList<>()).stream()).collect(Collectors.toSet());
    }

    public Map<String, ArrayList<String>> getPartialMap(Collection<String> departmentCodes) {
        return departmentCodeMap.entrySet().stream()
                .filter(e -> departmentCodes.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
