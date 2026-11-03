package uk.gov.cabinetoffice.csl.domain.learning.requiredLearning;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

import java.util.HashSet;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CourseWithLearningPeriods {

    private String title;
    private Map<String, LearningPeriodWithOrgs> learningPeriods;

    public void addAudience(Audience audience) {
        LearningPeriod lp = audience.getLearningPeriod();
        LearningPeriodWithOrgs learningPeriodWithOrgs = new LearningPeriodWithOrgs(lp.getStartDateAsDateTime(), lp.getEndDateAsDateTime(), new HashSet<>());
        learningPeriods.put(audience.getId(), learningPeriodWithOrgs);
    }
}
