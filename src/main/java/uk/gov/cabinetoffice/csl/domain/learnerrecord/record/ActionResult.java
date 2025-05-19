package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.service.messaging.model.IMessageMetadata;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.ArrayList;
import java.util.List;

@Data
public class ActionResult {

    private final List<IMessageMetadata> messages = new ArrayList<>();
    private final List<IEmail> emails = new ArrayList<>();
    private final LearnerRecordResults learnerRecordResults = new LearnerRecordResults();

    public void add(ActionResult actionResult) {
        this.messages.addAll(actionResult.getMessages());
        this.emails.addAll(actionResult.getEmails());
        this.learnerRecordResults.add(actionResult.getLearnerRecordResults());
    }

}
