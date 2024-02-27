package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class CourseFactory {

    private final LearningPeriodFactory learningPeriodFactory;

    public CourseFactory(LearningPeriodFactory learningPeriodFactory) {
        this.learningPeriodFactory = learningPeriodFactory;
    }

    public Course buildCourseData(Course course) {
        Collection<Audience> audiences = course.getAudiences();
        Map<String, LearningPeriod> departmentDeadlineMap = buildDepartmentDeadlineMap(audiences);
        course.setDepartmentDeadlineMap(departmentDeadlineMap);
        return course;
    }

    private Map<String, LearningPeriod> buildDepartmentDeadlineMap(Collection<Audience> audiences) {
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
