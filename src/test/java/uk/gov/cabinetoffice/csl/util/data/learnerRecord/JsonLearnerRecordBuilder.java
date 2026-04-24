package uk.gov.cabinetoffice.csl.util.data.learnerRecord;

import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

public class JsonLearnerRecordBuilder extends BaseJsonBuilder {

    private final String learnerId;
    private final String resourceId;

    public JsonLearnerRecordBuilder(String learnerId, String resourceId) {
        this.learnerId = learnerId;
        this.resourceId = resourceId;
    }

    public static JsonLearnerRecordBuilder create(String learnerId, String resourceId) {
        JsonLearnerRecordBuilder builder = new JsonLearnerRecordBuilder(learnerId, resourceId);
        builder.root.put("resourceId", resourceId)
                .put("learnerId", learnerId)
                .putObject("recordType").put("type", "COURSE");
        return builder;
    }

    public JsonLearnerRecordBuilder addLatestEvent(String eventType, String timestamp) {
        ObjectNode latestEvent = root.putObject("latestEvent")
                .put("learnerId", learnerId)
                .put("resourceId", resourceId);
        latestEvent.putObject("eventType")
                .put("eventType", eventType)
                .putObject("learnerRecordType")
                .put("type", "COURSE");
        latestEvent.put("eventTimestamp", timestamp)
                .putObject("eventSource")
                .put("source", "csl_source_id");

        return this;
    }

}
