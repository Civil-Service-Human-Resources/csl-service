package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CourseFactoryTest {

    LearningPeriod genericLearningPeriod = new LearningPeriod(
            LocalDateTime.MIN, LocalDateTime.MAX
    );
    LearningPeriodFactory learningPeriodFactory = mock(LearningPeriodFactory.class);
    CourseFactory courseFactory = new CourseFactory(learningPeriodFactory);

    @BeforeEach
    public void before() {
        when(learningPeriodFactory.buildLearningPeriod(any())).thenReturn(genericLearningPeriod);
    }

    @Test
    void testBuildDepartmentDeadlineMap() {
        Audience requiredAudienceForCO = mock(Audience.class);
        when(requiredAudienceForCO.getDepartments()).thenReturn(List.of("CO"));
        when(requiredAudienceForCO.isRequired()).thenReturn(true);
        Audience nonRequiredAudienceForHMRC = mock(Audience.class);
        when(nonRequiredAudienceForHMRC.getDepartments()).thenReturn(List.of("HMRC"));
        when(nonRequiredAudienceForHMRC.isRequired()).thenReturn(false);
        Audience nonRequiredAudienceForGrade7 = mock(Audience.class);
        when(nonRequiredAudienceForGrade7.getDepartments()).thenReturn(List.of());
        when(nonRequiredAudienceForGrade7.getGrades()).thenReturn(List.of("G7"));
        when(nonRequiredAudienceForGrade7.isRequired()).thenReturn(false);
        Collection<Audience> audiences = List.of(
                requiredAudienceForCO, nonRequiredAudienceForHMRC, nonRequiredAudienceForGrade7
        );

        Map<String, LearningPeriod> result = courseFactory.buildDepartmentDeadlineMap(audiences);

        assertEquals(1, result.size());
        assertEquals(result.get("CO"), genericLearningPeriod);
        assertNull(result.get("HMRC"));
    }
}
