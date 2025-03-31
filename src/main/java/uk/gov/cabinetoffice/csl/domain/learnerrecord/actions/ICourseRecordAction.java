package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.Collection;

public interface ICourseRecordAction {

    CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord);

    CourseRecord generateNewCourseRecord();

    String getCourseId();

    String getUserId();

    CourseRecordId getCourseRecordId();

    Collection<IMessageMetadata> getMessages();

    Collection<IEmail> getEmails();

    String getAction();
}
