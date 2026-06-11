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
        builder.root.put("isCategoryTag", false);
        builder.root.put("isArchived", false);
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
        builder.root.put("isCategoryTag", false);
        builder.root.put("isArchived", false);
        builder.root.put("parentId", parentId);
        builder.root.put("parentName", parentName);
        builder.root.put("createdTimestamp", createdTimestamp);
        builder.root.put("updatedTimestamp", createdTimestamp);
        return builder;
    }

}
