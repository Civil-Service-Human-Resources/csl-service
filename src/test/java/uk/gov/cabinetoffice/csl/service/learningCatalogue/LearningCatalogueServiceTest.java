package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LearningCatalogueServiceTest {

    @Mock
    RequiredLearningMapCache requiredLearningMapCache;

    @Mock
    ObjectCache<Course> courseObjectCache;

    @InjectMocks
    LearningCatalogueService learningCatalogueService;

    @Test
    void removeCourseFromCache() {
        learningCatalogueService.removeCourseFromCache("course2");

        verify(requiredLearningMapCache, atMostOnce()).evict();
        verify(courseObjectCache, atMostOnce()).evict("course2");

    }
}
