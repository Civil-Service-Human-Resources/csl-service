package uk.gov.cabinetoffice.csl.controller.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.CourseWithLearningPeriods;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.LearningPeriodWithOrgs;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.TuplePair;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RequiredLearningCourseMapResponseTest extends TestDataService {

    private RequiredLearningCourseMapResponse responseMap;

    @BeforeEach
    void setUp() {
        responseMap = new RequiredLearningCourseMapResponse(new HashMap<>());
    }

    private void populateResponse() {
        Audience audience = generateRequiredAudience("CO");
        LearningPeriod lp = new LearningPeriod(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1));
        audience.setLearningPeriod(lp);

        Audience audience2 = generateRequiredAudience("HMRC");
        audience2.setId("audId2");
        audience2.getDepartments().add("COD");
        LearningPeriod lp2 = new LearningPeriod(LocalDate.of(2022, 1, 1), LocalDate.of(2023, 1, 1));
        audience2.setLearningPeriod(lp2);

        responseMap.addAudiences("course-1", "Course 1", List.of(audience, audience2));

        Audience audience3 = generateRequiredAudience("HMRC");
        audience3.setId("audId3");
        audience3.getDepartments().add("COD");
        audience3.getDepartments().add("DWP");
        LearningPeriod lp3 = new LearningPeriod(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 1));
        audience3.setLearningPeriod(lp3);

        responseMap.addAudiences("course-2", "Course 2", List.of(audience3));
    }

    @Test
    void testAddAudiencesPopulatesMapAndOrgMap() {
        populateResponse();
        assertTrue(responseMap.containsKey("course-1"));
        assertEquals("Course 1", responseMap.get("course-1").getTitle());

        Map<String, Collection<TuplePair<String, String>>> orgMap = responseMap.getOrgCodeToCourseAudienceIdMap();
        assertEquals(4, orgMap.size());
        List<TuplePair<String, String>> coCollection = new ArrayList<>(orgMap.get("CO"));
        assertEquals(List.of(new TuplePair<>("course-1", "audId")), coCollection);

        List<TuplePair<String, String>> codCollection = new ArrayList<>(orgMap.get("COD"));
        assertEquals(List.of(new TuplePair<>("course-1", "audId"), new TuplePair<>("course-1", "audId2"), new TuplePair<>("course-2", "audId3")), codCollection);

        List<TuplePair<String, String>> hmrcCollection = new ArrayList<>(orgMap.get("HMRC"));
        assertEquals(List.of(new TuplePair<>("course-1", "audId2"), new TuplePair<>("course-2", "audId3")), hmrcCollection);

        List<TuplePair<String, String>> dwpCollection = new ArrayList<>(orgMap.get("DWP"));
        assertEquals(List.of(new TuplePair<>("course-2", "audId3")), dwpCollection);
    }

    @Test
    void testBuildResponseWithHierarchies() {
        populateResponse();
        OrganisationalUnit co = new OrganisationalUnit();
        co.setCode("CO");
        co.setId(1L);
        OrganisationalUnit cod = new OrganisationalUnit();
        cod.setCode("COD");
        cod.setId(2L);
        OrganisationalUnit hmrc = new OrganisationalUnit();
        hmrc.setCode("HMRC");
        hmrc.setId(3L);
        OrganisationalUnit dwp = new OrganisationalUnit();
        dwp.setCode("DWP");
        dwp.setId(4L);
        List.of(List.of(co), List.of(cod, co), List.of(hmrc), List.of(dwp))
                .forEach(hierarchy -> responseMap.checkAndStoreOrganisationHierarchy(hierarchy));

        assertEquals(2, responseMap.size());

        CourseWithLearningPeriods course1WithLearningPeriods = responseMap.get("course-1");
        assertEquals("Course 1", course1WithLearningPeriods.getTitle());

        Map<String, LearningPeriodWithOrgs> course1LearningPeriods = course1WithLearningPeriods.getLearningPeriods();
        assertEquals(2, course1LearningPeriods.size());

        LearningPeriodWithOrgs audIdLearningPeriod = course1LearningPeriods.get("audId");
        assertEquals(Set.of(1), audIdLearningPeriod.getOrganisationIds());
        LearningPeriodWithOrgs audId2LearningPeriod = course1LearningPeriods.get("audId2");
        assertEquals(Set.of(2, 3), audId2LearningPeriod.getOrganisationIds());

        CourseWithLearningPeriods course2WithLearningPeriods = responseMap.get("course-2");
        assertEquals("Course 2", course2WithLearningPeriods.getTitle());

        Map<String, LearningPeriodWithOrgs> course2LearningPeriods = course2WithLearningPeriods.getLearningPeriods();
        assertEquals(1, course2LearningPeriods.size());

        LearningPeriodWithOrgs audId3LearningPeriod = course2LearningPeriods.get("audId3");
        assertEquals(Set.of(3, 2, 4), audId3LearningPeriod.getOrganisationIds());

    }

}
