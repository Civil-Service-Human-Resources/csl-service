package uk.gov.cabinetoffice.csl.util.data.learnerRecord;

import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

public class JsonModuleRecordBuilder extends BaseJsonBuilder {

    public static JsonModuleRecordBuilder create(String moduleId, String courseId, String userId, String type, String createdAt) {
        JsonModuleRecordBuilder builder = new JsonModuleRecordBuilder();
        builder.root.put("moduleId", moduleId)
                .put("courseId", courseId)
                .put("userId", userId)
                .put("moduleType", type)
                .put("title", moduleId)
                .put("createdAt", createdAt)
                .put("state", "NULL");
        return builder;
    }

    public JsonModuleRecordBuilder addCompletionDate(String completionDate, String updatedAt) {
        return addCompletionDate(completionDate).addUpdatedAt(updatedAt);
    }

    public JsonModuleRecordBuilder addCompletionDate(String completionDate) {
        root.put("completionDate", completionDate);
        return this;
    }

    public JsonModuleRecordBuilder addUpdatedAt(String updatedAt) {
        root.put("updatedAt", updatedAt);
        return this;
    }

    public JsonModuleRecordBuilder addState(String state) {
        root.put("state", state);
        return this;
    }

}
