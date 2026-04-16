package uk.gov.cabinetoffice.csl.util.data.catalogue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        builder.root.put("shortDescription", title);
        builder.root.putArray("audiences");
        builder.root.putArray("modules");
        return builder;
    }

    private ObjectNode createAudience(String departmentCode, LocalDate requiredBy) {
        ArrayNode aud = getOrCreateArray("audiences");
        ObjectNode item = aud.addObject();
        item.putArray("departments").add(departmentCode);
        item.put("requiredBy", requiredBy.format(DateTimeFormatter.ISO_DATE_TIME))
                .put("item", "REQUIRED_LEARNING");
        return item;
    }

    public JsonCourseBuilder addDepartmentRequiredLearning(String departmentCode, LocalDate requiredBy) {
        createAudience(departmentCode, requiredBy);
        return this;
    }

    public JsonCourseBuilder addDepartmentRequiredLearning(String departmentCode, LocalDate requiredBy, String frequency) {
        ObjectNode audience = createAudience(departmentCode, requiredBy);
        audience.put("frequency", frequency);
        return this;
    }

    private ObjectNode createModule(String type, String id, String title, boolean optional, int duration) {
        ArrayNode modules = getOrCreateArray("modules");
        return modules.addObject()
                .put("id", id)
                .put("courseId", courseId)
                .put("title", title)
                .put("description", title)
                .put("type", type)
                .put("moduleType", type)
                .put("duration", duration)
                .put("optional", optional);
    }

    public JsonCourseBuilder addLinkModule(String id, String title, boolean optional, int duration) {
        createModule("link", id, title, optional, duration)
                .put("link", "https://generic.com");
        return this;
    }

    public JsonCourseBuilder addElearningModule(String id, String title, boolean optional, int duration) {
        createModule("elearning", id, title, optional, duration)
                .put("link", "https://generic.com");
        return this;
    }
}
