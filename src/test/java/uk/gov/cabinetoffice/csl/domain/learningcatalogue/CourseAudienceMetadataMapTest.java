package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.AreaOfWork;
import uk.gov.cabinetoffice.csl.domain.csrs.BasicOrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.Interest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CourseAudienceMetadataMapTest {

    @Test
    void testFilterForUser() {

        CourseAudienceMetadataMap map = new CourseAudienceMetadataMap(
                Map.of("CO", List.of("course1", "course2"), "HMRC", List.of("course3"), "COD", List.of("course3", "course10")),
                Map.of("Analysis", List.of("course1", "course4"), "Finance", List.of("course3", "course5", "course6")),
                Map.of("EU", List.of("course5"), "Parliament", List.of("course4", "course7"))
        );

        User user = new User("UID");
        user.setProfessionName("Analysis");
        user.setProfessionId(2);
        user.setOtherAreasOfWork(List.of(new AreaOfWork(1L, "Finance")));
        user.setDepartmentHierarchy(new ArrayList<>(List.of(
                new BasicOrganisationalUnit(1, "COD", "Cabinet Office Digital"),
                new BasicOrganisationalUnit(2, "CO", "Cabinet Office")
        )));
        user.setInterests(List.of(new Interest(1L, "Parliament")));

        LinkedHashMap<String, Collection<String>> results = map.filterForUser(user, List.of("course4"));

        assertEquals("Cabinet Office Digital", results.keySet().stream().findFirst().get());

        assertEquals(Map.of(
                "Cabinet Office Digital", List.of("course3", "course10", "course1", "course2"),
                "Analysis", List.of(),
                "Finance", List.of("course5", "course6"),
                "Parliament", List.of("course7")
        ), results);

    }
}
