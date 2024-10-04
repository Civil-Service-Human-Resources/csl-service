package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learning.DisplayAudience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DisplayAudienceFactoryTest {

    DisplayAudienceFactory displayAudienceFactory = new DisplayAudienceFactory();

    TestDataService testDataService = new TestDataService();

    @Test
    public void testGenerateDisplayAudience() {
        Course course = new Course();
        Audience audience = new Audience();
        audience.setName("AudienceName");
        audience.setFrequency("P1Y");
        audience.setLearningPeriod(new LearningPeriod(LocalDate.MIN, LocalDate.MAX));

        course.setAudiences(List.of(audience));
        course.setDepartmentCodeToRequiredAudienceMap(Map.of("DWP", 0));

        User user = testDataService.generateUser();

        DisplayAudience result = displayAudienceFactory.generateDisplayAudience(course, user);
        assertEquals("AudienceName", result.getName());
        assertEquals("1 years, 0 months", result.getFrequency());
        assertEquals(LocalDate.MIN, result.getLearningPeriod().getStartDate());
        assertEquals(LocalDate.MAX, result.getLearningPeriod().getEndDate());
    }

}
