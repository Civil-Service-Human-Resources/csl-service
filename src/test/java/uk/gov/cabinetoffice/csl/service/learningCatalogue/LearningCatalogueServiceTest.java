package uk.gov.cabinetoffice.csl.service.learningCatalogue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.RequiredLearningMap;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

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

        RequiredLearningMap requiredLearningMap = new RequiredLearningMap();
        requiredLearningMap.setDepartmentCodeMap(Map.of("CO", new ArrayList<>(List.of("course1", "course2"))));
        when(requiredLearningMapCache.get()).thenReturn(requiredLearningMap);
        learningCatalogueService.removeCourseFromCache("course2");

        verify(requiredLearningMapCache, atMostOnce()).evict();
        verify(courseObjectCache, atMostOnce()).evict("course2");

    }
}
