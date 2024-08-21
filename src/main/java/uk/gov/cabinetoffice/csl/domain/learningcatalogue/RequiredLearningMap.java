package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequiredLearningMap implements Serializable {

    private Map<String, ArrayList<String>> departmentCodeMap;

    public Set<String> getRequiredLearningWithDepartmentCodes(Collection<String> depCodes) {
        return depCodes.stream().flatMap(code -> departmentCodeMap.getOrDefault(code, new ArrayList<>()).stream()).collect(Collectors.toSet());
    }

    public boolean doesCourseExistInMap(String courseId) {
        for (List<String> courseIds : departmentCodeMap.values()) {
            if (courseIds.contains(courseId)) {
                return true;
            }
        }
        return false;
    }
}
