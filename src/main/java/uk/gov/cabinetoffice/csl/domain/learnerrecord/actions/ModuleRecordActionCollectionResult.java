package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleRecordActionCollectionResult {
    private final List<ModuleRecord> newRecords = new ArrayList<>();
    private final List<ModuleRecord> updatedRecords = new ArrayList<>();
    private final List<IMessageMetadata> messages = new ArrayList<>();
    private final List<IEmail> emails = new ArrayList<>();
}
