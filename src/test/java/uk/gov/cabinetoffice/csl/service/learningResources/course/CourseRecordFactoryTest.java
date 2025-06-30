package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Preference;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ILearnerRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CourseRecordFactoryTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @InjectMocks
    private CourseRecordFactory courseRecordFactory;

    private CourseWithRecord generateCourseWithRecord(@Nullable ILearnerRecordActionType latestActionType) {
        LearnerRecord learnerRecord = null;
        if (latestActionType != null) {
            learnerRecord = new LearnerRecord();
            LearnerRecordEvent learnerRecordEvent = new LearnerRecordEvent();
            learnerRecordEvent.setActionType(latestActionType);
            learnerRecordEvent.setEventTimestamp(LocalDateTime.of(2025, 1, 1, 10, 0, 0));
            learnerRecord.setLatestEvent(learnerRecordEvent);
        }
        Module module1 = new Module();
        module1.setId("module1");
        Module module2 = new Module();
        module2.setId("module2");
        return new CourseWithRecord("userId", "courseId", "Course Title",
                List.of(module1, module2), learnerRecord);
    }

    private ModuleRecord generateSimpleModuleRecord(State state, LocalDateTime lastUpdated) {
        ModuleRecord moduleRecord = new ModuleRecord();
        moduleRecord.setState(state);
        moduleRecord.setUpdatedAt(lastUpdated);
        return moduleRecord;
    }

    @Test
    public void testCourseRecordMovedToLP() {
        CourseWithRecord courseWithRecord = generateCourseWithRecord(CourseRecordAction.MOVE_TO_LEARNING_PLAN);
        CourseRecord result = courseRecordFactory.transformToCourseRecord(courseWithRecord, List.of());
        assertEquals(Preference.LIKED, result.getPreference());
        assertEquals(State.NULL, result.getState());
        assertEquals(
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                result.getLastUpdated()
        );
        assertEquals("courseId", result.getCourseId());
        assertEquals("Course Title", result.getCourseTitle());
        assertEquals("userId", result.getUserId());
    }

    @Test
    public void testCourseRecordInProgress() {
        CourseWithRecord courseWithRecord = generateCourseWithRecord(CourseRecordAction.MOVE_TO_LEARNING_PLAN);
        CourseRecord result = courseRecordFactory.transformToCourseRecord(courseWithRecord, List.of(
                generateSimpleModuleRecord(State.IN_PROGRESS, LocalDateTime.of(2025, 2, 1, 10, 0, 0))
        ));
        assertEquals(State.IN_PROGRESS, result.getState());
        assertEquals(
                LocalDateTime.of(2025, 2, 1, 10, 0, 0),
                result.getLastUpdated()
        );
    }

    @Test
    public void testCourseRecordCompleted() {
        CourseWithRecord courseWithRecord = generateCourseWithRecord(CourseRecordAction.COMPLETE_COURSE);
        CourseRecord result = courseRecordFactory.transformToCourseRecord(courseWithRecord, List.of(
                generateSimpleModuleRecord(State.IN_PROGRESS, LocalDateTime.of(2025, 2, 1, 10, 0, 0))
        ));
        assertEquals(State.COMPLETED, result.getState());
        assertEquals(
                LocalDateTime.of(2025, 2, 1, 10, 0, 0),
                result.getLastUpdated()
        );
    }

    @Test
    public void testCourseRecordProgressedAndRemovedFromLP() {
        CourseWithRecord courseWithRecord = generateCourseWithRecord(CourseRecordAction.REMOVE_FROM_LEARNING_PLAN);
        CourseRecord result = courseRecordFactory.transformToCourseRecord(courseWithRecord, List.of(
                generateSimpleModuleRecord(State.IN_PROGRESS, LocalDateTime.of(2024, 1, 1, 10, 0, 0))
        ));
        assertEquals(State.ARCHIVED, result.getState());
        assertEquals(
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                result.getLastUpdated()
        );
    }

    @Test
    public void testCourseRecordSkipped() {
        CourseWithRecord courseWithRecord = generateCourseWithRecord(CourseRecordAction.MOVE_TO_LEARNING_PLAN);
        CourseRecord result = courseRecordFactory.transformToCourseRecord(courseWithRecord, List.of(
                generateSimpleModuleRecord(State.SKIPPED, LocalDateTime.of(2024, 1, 1, 10, 0, 0)),
                generateSimpleModuleRecord(State.IN_PROGRESS, LocalDateTime.of(2024, 1, 1, 10, 0, 0))
        ));
        assertEquals(State.SKIPPED, result.getState());
        assertEquals(
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                result.getLastUpdated()
        );
    }


}
