package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience.Type.OPEN;
import static uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience.Type.REQUIRED_LEARNING;

class CourseFactoryTest {

    LearningPeriod genericLearningPeriod = new LearningPeriod(
            LocalDate.MIN, LocalDate.MAX
    );
    LearningPeriodFactory learningPeriodFactory = mock(LearningPeriodFactory.class);
    CourseFactory courseFactory = new CourseFactory(learningPeriodFactory);

    @BeforeEach
    public void before() {
        when(learningPeriodFactory.buildLearningPeriod(any())).thenReturn(genericLearningPeriod);
    }

    @Test
    void testBuildRequiredLearningDepartmentMap() {
        Audience requiredAudienceForCO = new Audience();
        requiredAudienceForCO.setDepartments(List.of("CO"));
        requiredAudienceForCO.setRequiredBy(LocalDate.now());
        requiredAudienceForCO.setType(REQUIRED_LEARNING);

        Audience nonRequiredAudienceForHMRC = new Audience();
        nonRequiredAudienceForHMRC.setDepartments(List.of("HMRC"));
        nonRequiredAudienceForHMRC.setType(OPEN);

        Audience requiredAudienceForHMRC = new Audience();
        requiredAudienceForHMRC.setDepartments(List.of("HMRC"));
        requiredAudienceForHMRC.setRequiredBy(LocalDate.now());
        requiredAudienceForHMRC.setType(REQUIRED_LEARNING);

        Audience nonRequiredAudienceForGrade7 = new Audience();
        nonRequiredAudienceForGrade7.setGrades(List.of("G7"));
        nonRequiredAudienceForGrade7.setType(OPEN);

        List<Audience> audiences = List.of(
                requiredAudienceForCO, nonRequiredAudienceForHMRC,
                requiredAudienceForHMRC, nonRequiredAudienceForGrade7
        );

        Map<String, Integer> result = courseFactory.buildRequiredLearningDepartmentMap(audiences);

        assertEquals(2, result.size());
        assertEquals(result.get("CO"), 0);
        assertEquals(result.get("HMRC"), 2);
    }
}
