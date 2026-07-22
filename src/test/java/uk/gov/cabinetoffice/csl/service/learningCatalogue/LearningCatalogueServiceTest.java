package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.CourseLearningTagDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagDTO;
import uk.gov.cabinetoffice.csl.util.TtlObjectCache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningCatalogueServiceTest {

    @Mock
    LearningCatalogueCacheService learningCatalogueCacheService;

    @Mock
    TtlObjectCache<Course> courseObjectCache;

    @Mock
    ILearningCatalogueClient learningCatalogueClient;

    @InjectMocks
    LearningCatalogueService learningCatalogueService;

    @Test
    void removeCourseFromCache() {
        learningCatalogueService.removeCourseFromCache("course2");

        verify(learningCatalogueCacheService, atMostOnce()).evict();
        verify(courseObjectCache, atMostOnce()).evict("course2");
    }

    @Test
    void shouldAddLearningTagToCourse() {
        String courseId = "courseId";
        LearningTagDTO dto = new LearningTagDTO();
        CourseLearningTagDto expectedResult = new CourseLearningTagDto();

        when(learningCatalogueClient.addLearningTagToCourse(courseId, dto)).thenReturn(expectedResult);

        CourseLearningTagDto result = learningCatalogueService.addLearningTagToCourse(courseId, dto);

        assertEquals(expectedResult, result);
        verify(learningCatalogueClient).addLearningTagToCourse(courseId, dto);
    }
}
