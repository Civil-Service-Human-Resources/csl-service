package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayAudience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisplayAudienceFactoryTest {

    DisplayAudienceFactory displayAudienceFactory = new DisplayAudienceFactory();

    @Test
    public void testGenerateDisplayAudience() {
        Course course = new Course();
        Audience audience = new Audience();
        audience.setName("AudienceName");
        audience.setFrequency("P1Y");
        audience.setLearningPeriod(new LearningPeriod(LocalDate.MIN, LocalDate.MAX));

        course.setAudiences(List.of(audience));
        course.setDepartmentCodeToRequiredAudienceMap(Map.of("CODE2", 0));

        User user = new User("UserId");
        user.setDepartmentCodes(List.of("CODE1", "CODE2", "CODE3"));

        DisplayAudience result = displayAudienceFactory.generateDisplayAudience(course, user);
        assertEquals("AudienceName", result.getName());
        assertEquals("1 years, 0 months", result.getFrequency());
        assertEquals(LocalDate.MIN, result.getLearningPeriod().getStartDate());
        assertEquals(LocalDate.MAX, result.getLearningPeriod().getEndDate());
    }

}
