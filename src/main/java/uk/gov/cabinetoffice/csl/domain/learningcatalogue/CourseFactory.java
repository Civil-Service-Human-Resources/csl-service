package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseFactory {

    private final LearningPeriodFactory learningPeriodFactory;

    public Map<String, LearningPeriod> buildDepartmentDeadlineMap(Collection<Audience> audiences) {
        Map<String, LearningPeriod> map = new HashMap<>();
        audiences.forEach(a -> {
            if (a.isRequired() && !a.getDepartments().isEmpty()) {
                LearningPeriod learningPeriod = learningPeriodFactory.buildLearningPeriod(a);
                a.getDepartments().forEach(dep -> map.put(dep, learningPeriod));
            }
        });
        return map;
    }
}
