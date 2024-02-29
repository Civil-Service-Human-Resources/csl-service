package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.ICourseRecordActionType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CourseRecordActionProcessor implements ICourseRecordAction {

    protected final Course course;
    protected final User user;
    protected final Collection<IMessageMetadata> messages = new ArrayList<>();
    protected final ICourseRecordActionType actionType;

    protected CourseRecordActionProcessor(Course course, User user, ICourseRecordActionType actionType) {
        this.course = course;
        this.user = user;
        this.actionType = actionType;
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
    public Collection<IMessageMetadata> getMessages() {
        return this.messages;
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
