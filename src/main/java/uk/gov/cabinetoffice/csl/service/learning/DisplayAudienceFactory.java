package uk.gov.cabinetoffice.csl.service.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayAudience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

@Service
@Slf4j
public class DisplayAudienceFactory {

    public DisplayAudience generateDisplayAudience(Course course, User user) {
        for (String code : user.getDepartmentCodes()) {
            Audience audience = course.getRequiredAudienceWithDepCode(code);
            if (audience != null) {
                LearningPeriod learningPeriod = audience.getLearningPeriod();
                return new DisplayAudience(code, audience.getFrequencyAsString(), learningPeriod);
            }
        }
        return null;
    }

}
