package uk.gov.cabinetoffice.csl.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.courseCatalogue.ILearningCatalogueClient;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.util.CacheGetMultipleOp;
import uk.gov.cabinetoffice.csl.util.ObjectCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LearningCatalogueServiceTest {

    @Mock
    private ObjectCache<Course> cache;
    @Mock
    private ILearningCatalogueClient client;

    @InjectMocks
    private LearningCatalogueService learningCatalogueService;

    @Test
    void getCoursesWithFullCacheHit() {
        Course course1 = new Course();
        course1.setId("course1");
        Course course2 = new Course();
        course2.setId("course2");
        Course course3 = new Course();
        course3.setId("course3");

        CacheGetMultipleOp<Course> cacheResult = new CacheGetMultipleOp<>(List.of(),
                new ArrayList<>(Arrays.asList(course1, course2, course3)));

        when(cache.getMultiple(List.of("course1", "course2", "course3"))).thenReturn(cacheResult);

        List<Course> result = learningCatalogueService.getCourses(List.of("course1", "course2", "course3"));
        assertEquals("course1", result.get(0).getCacheableId());
        assertEquals("course2", result.get(1).getCacheableId());
        assertEquals("course3", result.get(2).getCacheableId());
    }

    @Test
    void getCoursesWithPartialCacheHit() {
        Course course1 = new Course();
        course1.setId("course1");
        Course course2 = new Course();
        course2.setId("course2");
        Course course3 = new Course();
        course3.setId("cache-miss-course3");

        CacheGetMultipleOp<Course> cacheResult = new CacheGetMultipleOp<>(List.of("cache-miss-course3"),
                new ArrayList<>(Arrays.asList(course1, course2)));

        when(cache.getMultiple(List.of("course1", "course2", "cache-miss-course3"))).thenReturn(cacheResult);
        when(client.getCourses(List.of("cache-miss-course3"))).thenReturn(List.of(course3));

        List<Course> result = learningCatalogueService.getCourses(List.of("course1", "course2", "cache-miss-course3"));
        assertEquals("course1", result.get(0).getCacheableId());
        assertEquals("course2", result.get(1).getCacheableId());
        assertEquals("cache-miss-course3", result.get(2).getCacheableId());
        verify(cache, atLeastOnce()).put(course3);
    }
}
