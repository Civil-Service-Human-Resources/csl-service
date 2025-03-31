package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CourseRecordActionProcessor implements ICourseRecordAction {

    protected final UtilService utilService;
    protected final Course course;
    protected final User user;
    protected final ICourseRecordActionType actionType;
    private final Collection<IMessageMetadata> messages;
    private final Collection<IEmail> emails;

    protected CourseRecordActionProcessor(UtilService utilService, Course course, User user,
                                          ICourseRecordActionType actionType) {
        this.utilService = utilService;
        this.course = course;
        this.user = user;
        this.actionType = actionType;
        this.messages = new ArrayList<>();
        this.emails = new ArrayList<>();
    }

    @Override
    public String getCourseId() {
        return course.getId();
    }

    @Override
    public String getUserId() {
        return user.getId();
    }

    @Override
    public CourseRecordId getCourseRecordId() {
        return new CourseRecordId(getUserId(), getCourseId());
    }

    @Override
    public Collection<IMessageMetadata> getMessages() {
        return this.messages;
    }

    protected void addMessage(IMessageMetadata message) {
        this.messages.add(message);
    }

    @Override
    public Collection<IEmail> getEmails() {
        return this.emails;
    }

    protected void addEmail(IEmail message) {
        this.emails.add(message);
    }

    @Override
    public String getAction() {
        return actionType.getDescription();
    }

    protected CourseRecord createCourseRecord() {
        CourseRecord courseRecord = new CourseRecord(course.getId(), user.getId(), course.getTitle());
        courseRecord.setRequired(course.isMandatoryLearningForUser(user));
        return courseRecord;
    }

    @Override
    public String toString() {
        return String.format("Action: '%s' | Learner ID: %s | Course ID: %s", getAction(), user.getId(), course.getId());
    }
}
