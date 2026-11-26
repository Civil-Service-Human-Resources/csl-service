package uk.gov.cabinetoffice.csl.service.learning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayAudience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

import java.util.Optional;

@Service
@Slf4j
public class DisplayAudienceFactory {

    public DisplayAudience generateDisplayAudience(Course course, User user) {
        Optional<Audience> optionalAudience = course.getAudienceForDepartmentHierarchy(user.getDepartmentCodes());
        if (optionalAudience.isPresent()) {
            Audience audience = optionalAudience.get();
            LearningPeriod learningPeriod = audience.getLearningPeriod();
            String frequency = audience.getFrequencyAsString();
            return new DisplayAudience(audience.getName(), frequency == null ? "None" : frequency, learningPeriod);
        }
        return null;
    }

}
