package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LearningTagMapTest {

    private final TestDataService testDataService = new TestDataService();

    private final LearningTagMap map = LearningTagMap.buildFromList(testDataService.createLearningTagList());

    @Test
    void testUpdate() {
        LearningTag tag = map.get(1L);
        tag.setUrlSlug("new-slug");
        map.rebuildHierarchy(tag);
        assertEquals(1L, map.getWithUrl("new-slug").getId());
        assertThrows(NotFoundException.class, () -> map.getWithUrl("TagName1"));
    }
}
