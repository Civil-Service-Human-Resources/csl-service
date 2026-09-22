package uk.gov.cabinetoffice.csl.util.data.catalogue;

import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

public class JsonLearningTagBuilder extends BaseJsonBuilder {

    public static JsonLearningTagBuilder create(Long id, Long parentId, String parentName, String createdTimestamp) {
        JsonLearningTagBuilder builder = new JsonLearningTagBuilder();
        String name = String.format("TagName%s", id);
        String code = String.format("TAGN%s", id);
        builder.root.put("id", id);
        builder.root.put("name", name);
        builder.root.put("description", String.format("%s description", name));
        builder.root.put("code", code);
        builder.root.put("urlSlug", code);
        builder.root.put("category", false);
        builder.root.put("archived", false);
        builder.root.put("parentId", parentId);
        builder.root.put("parentName", parentName);
        builder.root.put("createdTimestamp", createdTimestamp);
        builder.root.put("updatedTimestamp", createdTimestamp);
        return builder;
    }

    public static JsonLearningTagBuilder create(Long id, String name, String code, Long parentId, String parentName,
                                                String createdTimestamp) {
        JsonLearningTagBuilder builder = new JsonLearningTagBuilder();
        builder.root.put("id", id);
        builder.root.put("name", name);
        builder.root.put("description", String.format("%s description", name));
        builder.root.put("code", code);
        builder.root.put("urlSlug", code);
        builder.root.put("category", false);
        builder.root.put("archived", false);
        builder.root.put("parentId", parentId);
        builder.root.put("parentName", parentName);
        builder.root.put("createdTimestamp", createdTimestamp);
        builder.root.put("updatedTimestamp", createdTimestamp);
        return builder;
    }

    public JsonLearningTagBuilder isArchived() {
        root.put("archived", true);
        return this;
    }

    public JsonLearningTagBuilder isCategory() {
        root.put("category", true);
        return this;
    }

    public JsonLearningTagBuilder courseCount(Integer count) {
        root.put("courseCount", count);
        return this;
    }

    public JsonLearningTagBuilder linkCount(Integer count) {
        root.put("linkCount", count);
        return this;
    }

}
