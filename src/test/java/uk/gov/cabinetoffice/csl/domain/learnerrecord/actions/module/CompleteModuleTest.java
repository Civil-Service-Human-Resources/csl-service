package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;
import uk.gov.cabinetoffice.csl.service.notification.messages.NotifyLineManagerCompletedLearning;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompleteModuleTest extends BaseModuleRecordActionTest<CompleteModule> {

    @Override
    protected CompleteModule buildProcessor() {
        return new CompleteModule(utilService, courseWithModule, user, this.utilService.getNowDateTime());
    }

    @Test
    public void testCompleteModule() {
        CourseRecord cr = generateCourseRecord(true);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        assertEquals(State.COMPLETED, cr.getModuleRecord(getModuleId()).get().getState());
        assertTrue(actionUnderTest.getMessages().stream().findFirst().isPresent());
        CourseCompletionMessage courseCompletionMessage = (CourseCompletionMessage) actionUnderTest.getMessages().stream().findFirst().get();
        assertEquals(getUserId(), courseCompletionMessage.getUserId());
        assertEquals(getUserEmail(), courseCompletionMessage.getUserEmail());
        assertEquals(getCourseTitle(), courseCompletionMessage.getCourseTitle());
        assertEquals(getCourseId(), courseCompletionMessage.getCourseId());
        assertEquals(getOrganisationalUnit().getId().intValue(), courseCompletionMessage.getOrganisationId());
        assertEquals(getProfession().getId().intValue(), courseCompletionMessage.getProfessionId());
        assertEquals(getGrade().getId().intValue(), courseCompletionMessage.getGradeId());
    }

    @Test
    public void testCompleteRequiredLearningModule() {
        CourseRecord cr = generateCourseRecord(true);
        cr = actionUnderTest.applyUpdatesToCourseRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        assertEquals(State.COMPLETED, cr.getModuleRecord(getModuleId()).get().getState());
        assertTrue(actionUnderTest.getMessages().stream().findFirst().isPresent());
        CourseCompletionMessage courseCompletionMessage = (CourseCompletionMessage) actionUnderTest.getMessages().stream().findFirst().get();
        assertEquals(getUserId(), courseCompletionMessage.getUserId());
        assertEquals(getUserEmail(), courseCompletionMessage.getUserEmail());
        assertEquals(getCourseTitle(), courseCompletionMessage.getCourseTitle());
        assertEquals(getCourseId(), courseCompletionMessage.getCourseId());
        assertEquals(getOrganisationalUnit().getId().intValue(), courseCompletionMessage.getOrganisationId());
        assertEquals(getProfession().getId().intValue(), courseCompletionMessage.getProfessionId());
        assertEquals(getGrade().getId().intValue(), courseCompletionMessage.getGradeId());
    }

    @Test
    public void testCompleteRequiredLearningModuleForRequiredLearning() {
        Course requiredCourse = generateCourse(true, false);
        Audience requiredAudience = generateRequiredAudience(user.getDepartmentCodes().get(0));
        requiredAudience.setLearningPeriod(new LearningPeriod(LocalDate.MIN, LocalDate.MAX));
        requiredCourse.setAudiences(List.of(requiredAudience));
        requiredCourse.setDepartmentCodeToRequiredAudienceMap(Map.of(user.getDepartmentCodes().get(0), 0));
        Module requiredModule = requiredCourse.getModule(getModuleId());
        CourseWithModule requiredCourseWithModule = new CourseWithModule(requiredCourse, requiredModule);
        CompleteModule action = new CompleteModule(this.utilService, requiredCourseWithModule, user, this.utilService.getNowDateTime());
        CourseRecord cr = generateCourseRecord(true);
        cr = action.applyUpdatesToCourseRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        assertEquals(State.COMPLETED, cr.getModuleRecord(getModuleId()).get().getState());
        assertTrue(action.getEmails().stream().findFirst().isPresent());
        NotifyLineManagerCompletedLearning email = (NotifyLineManagerCompletedLearning) action.getEmails().stream().findFirst().get();
        assertEquals(getUserEmail(), email.getLearnerEmail());
        assertEquals(getLearnerFirstName(), email.getLearnerName());
        assertEquals(getLineManagerEmail(), email.getRecipient());
        assertEquals(getLineManagerName(), email.getLineManagerName());
        assertEquals(getCourseTitle(), email.getCourseTitle());
    }

    @Test
    public void testCompleteRequiredLearningModuleForRequiredLearningNoLineManager() {
        Course requiredCourse = generateCourse(true, false);
        User noLMUser = generateUser();
        noLMUser.setLineManagerEmail(null);
        noLMUser.setLineManagerName(null);
        Audience requiredAudience = generateRequiredAudience(noLMUser.getDepartmentCodes().get(0));
        requiredAudience.setLearningPeriod(new LearningPeriod(LocalDate.MIN, LocalDate.MAX));
        requiredCourse.setAudiences(List.of(requiredAudience));
        requiredCourse.setDepartmentCodeToRequiredAudienceMap(Map.of(noLMUser.getDepartmentCodes().get(0), 0));
        Module requiredModule = requiredCourse.getModule(getModuleId());
        CourseWithModule requiredCourseWithModule = new CourseWithModule(requiredCourse, requiredModule);
        CompleteModule action = new CompleteModule(this.utilService, requiredCourseWithModule, noLMUser, this.utilService.getNowDateTime());
        CourseRecord cr = generateCourseRecord(true);
        cr = action.applyUpdatesToCourseRecord(cr);
        assertEquals(State.COMPLETED, cr.getState());
        assertEquals(State.COMPLETED, cr.getModuleRecord(getModuleId()).get().getState());
        assertTrue(action.getEmails().isEmpty());
    }
}
