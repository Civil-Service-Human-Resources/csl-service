package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import lombok.Data;
import uk.gov.cabinetoffice.csl.service.notification.messages.IEmail;

import java.util.ArrayList;
import java.util.List;

@Data
public class ActionResult {

    private final List<Object> messages = new ArrayList<>();
    private final List<IEmail> emails = new ArrayList<>();
    private final LearnerRecordResults learnerRecordResults = new LearnerRecordResults();

}
