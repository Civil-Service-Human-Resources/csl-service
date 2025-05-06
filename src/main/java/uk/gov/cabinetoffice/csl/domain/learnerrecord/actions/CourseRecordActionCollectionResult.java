package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CourseRecordActionCollectionResult {
    private final List<CourseRecord> newRecords = new ArrayList<>();
    private final Map<String, CourseRecord> updatedRecords = new HashMap<>();
    private final List<IMessageMetadata> messages = new ArrayList<>();
    private final List<IEmail> emails = new ArrayList<>();
}
