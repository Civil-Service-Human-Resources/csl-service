package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;

import java.util.Collection;

public interface ICourseRecordAction {

    CourseRecord applyUpdatesToCourseRecord(CourseRecord courseRecord);

    CourseRecord generateNewCourseRecord();

    String getCourseId();

    String getUserId();

    Collection<IMessageMetadata> getMessages();

    String getAction();
}
