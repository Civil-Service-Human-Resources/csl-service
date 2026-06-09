package uk.gov.cabinetoffice.csl.util.data.catalogue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

import java.math.BigDecimal;

public class JsonCourseBuilder extends BaseJsonBuilder {

    private final String courseId;

    public JsonCourseBuilder(String courseId) {
        this.courseId = courseId;
    }

    public static JsonCourseBuilder create(String courseId, String title) {
        JsonCourseBuilder builder = new JsonCourseBuilder(courseId);
        builder.root.put("id", courseId);
        builder.root.put("title", title);
        builder.root.put("description", title);
        builder.root.put("shortDescription", String.format("%s short description", title));
        builder.root.put("visibility", "PUBLIC");
        builder.root.put("status", "Published");
        builder.root.putArray("audiences");
        builder.root.putArray("modules");
        return builder;
    }

    private ObjectNode createRequiredAudience(String departmentCode, String requiredBy) {
        ArrayNode aud = getOrCreateArray("audiences");
        ObjectNode item = aud.addObject();
        item.putArray("departments").add(departmentCode);
        item.put("requiredBy", requiredBy)
                .put("type", "REQUIRED_LEARNING");
        return item;
    }

    public JsonCourseBuilder createBlankAudience() {
        ArrayNode aud = getOrCreateArray("audiences");
        aud.addObject().put("type", "OPEN");
        return this;
    }

    public JsonCourseBuilder addDepartmentRequiredLearning(String departmentCode, String requiredBy) {
        createRequiredAudience(departmentCode, requiredBy);
        return this;
    }

    public JsonCourseBuilder addDepartmentRequiredLearning(String departmentCode, String requiredBy, String frequency) {
        ObjectNode audience = createRequiredAudience(departmentCode, requiredBy);
        audience.put("frequency", frequency);
        return this;
    }

    public JsonCourseBuilder addModule(String type, String id, String title, boolean optional, int duration) {
        getOrCreateArray("modules").add(JsonModuleBuilder.create(type, id, courseId, title, optional, duration).get());
        return this;
    }

    public JsonCourseBuilder addFaceToFaceModule(String id, String title, boolean optional, int duration,
                                                 String eventId, BigDecimal cost, DateRangeJsonValues... dateRanges) {
        getOrCreateArray("modules").add(JsonModuleBuilder.create("face-to-face", id, courseId, title, optional, duration)
                .addEvent(eventId, cost, dateRanges).get());
        return this;
    }

    public JsonCourseBuilder addLinkModule(String id, String title, boolean optional, int duration) {
        return this.addModule("link", id, title, optional, duration);
    }

    public JsonCourseBuilder addFileModule(String id, String title, boolean optional, int duration) {
        return this.addModule("file", id, title, optional, duration);
    }

    public JsonCourseBuilder addElearningModule(String id, String title, boolean optional, int duration) {
        return this.addModule("elearning", id, title, optional, duration);
    }
}
