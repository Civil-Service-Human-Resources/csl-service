package uk.gov.cabinetoffice.csl.service.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.RequiredLearning;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.service.LearnerRecordDataUtils;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequiredLearningServiceTest {

    @Mock
    private LearnerRecordDataUtils learnerRecordDataUtils;
    @Mock
    private LearningCatalogueService learningCatalogueService;
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private RequiredLearningService requiredLearningService;

    User user = mock(User.class);

    {
        when(user.getDepartmentCodes()).thenReturn(List.of("CO"));
    }

    // Course 1: User has completed on 2025-1-1
    Course course1 = mock(Course.class);

    {
        when(course1.getId()).thenReturn("course1");
        when(course1.shouldBeDisplayed()).thenReturn(true);
        when(course1.getLearningPeriodForUser(user)).thenReturn(Optional.of(new LearningPeriod(
                LocalDate.of(2024, 6, 1), LocalDate.of(2025, 6, 1)
        )));
    }

    LocalDateTime courseCompletion1 = LocalDateTime.of(2025, 1, 1, 10, 0, 0, 0);

    // Course 2: User has not completed
    Course course2 = mock(Course.class);

    {
        when(course2.getId()).thenReturn("course2");
        when(course2.getTitle()).thenReturn("course 2");
        when(course2.getShortDescription()).thenReturn("short description for course 2");
        when(course2.getCourseType()).thenReturn("blended");
        when(course2.getDurationInSeconds()).thenReturn(3600);
        when(course2.shouldBeDisplayed()).thenReturn(true);
        when(course2.getLearningPeriodForUser(user)).thenReturn(Optional.of(new LearningPeriod(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 1)
        )));
        when(course2.getRequiredModuleIdsForCompletion()).thenReturn(List.of("m1", "m2"));
    }

    LocalDateTime courseCompletion2 = LocalDateTime.of(2024, 1, 1, 10, 0, 0, 0);

    ModuleRecord moduleRecord1 = mock(ModuleRecord.class);

    {
        when(moduleRecord1.getModuleId()).thenReturn("m1");
        when(moduleRecord1.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 10, 0, 0, 0));
    }

    ModuleRecord moduleRecord2 = mock(ModuleRecord.class);

    {
        when(moduleRecord2.getModuleId()).thenReturn("m2");
        when(moduleRecord2.getUpdatedAt()).thenReturn(LocalDateTime.of(2025, 2, 1, 10, 0, 0, 0));
    }

    @Test
    void getRequiredLearning() {
        when(userDetailsService.getUserWithUid("userId")).thenReturn(user);
        when(learningCatalogueService.getRequiredLearningForDepartments(List.of("CO"))).thenReturn(List.of(course1, course2));
        when(learnerRecordDataUtils.getCompletionDatesForCourses("userId", List.of("course1", "course2"))).thenReturn(
                Map.of("course1", courseCompletion1, "course2", courseCompletion2)
        );
        ModuleRecordCollection moduleRecords = new ModuleRecordCollection();
        moduleRecords.addAll(List.of(moduleRecord1, moduleRecord2));
        moduleRecords.setLatestUpdatedDate(LocalDateTime.of(2025, 2, 1, 10, 0, 0, 0));
        when(learnerRecordDataUtils.getModuleRecordsForCourses(List.of("course2"), List.of(
                new ModuleRecordResourceId("userId", "m1"),
                new ModuleRecordResourceId("userId", "m2")
        ))).thenReturn(Map.of("course2", moduleRecords));
        RequiredLearning result = requiredLearningService.getRequiredLearning("userId", false);
        assertEquals(1, result.getCourses().size());
        assertEquals("course2", result.getCourses().get(0).getId());
        assertEquals("course 2", result.getCourses().get(0).getTitle());
        assertEquals("short description for course 2", result.getCourses().get(0).getShortDescription());
        assertEquals("blended", result.getCourses().get(0).getType());
        assertEquals(3600, result.getCourses().get(0).getDuration());
        assertEquals(LocalDate.of(2025, 6, 1), result.getCourses().get(0).getDueBy());
        assertEquals(State.IN_PROGRESS, result.getCourses().get(0).getStatus());
    }
}
