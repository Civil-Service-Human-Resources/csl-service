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
        builder.root.put("learningOutcomes", String.format("%s learning outcomes", title));
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
        item.put("name", "Required audience name")
                .put("requiredBy", requiredBy)
                .put("type", "REQUIRED_LEARNING")
                .put("id", "requiredAudId");
        return item;
    }

    public ObjectNode createBlankAudience() {
        ArrayNode aud = getOrCreateArray("audiences");
        return aud.addObject().put("type", "OPEN")
                .put("id", "blankAudId")
                .put("name", "Blank audience name");
    }

    public JsonCourseBuilder addBlankAudience() {
        createBlankAudience();
        return this;
    }

    public JsonCourseBuilder addAudience(String[] grades, String[] areasOfWork, String[] interests) {
        ObjectNode item = createBlankAudience();
        ArrayNode gradesArray = item.putArray("grades");
        for (String grade : grades) {
            gradesArray.add(grade);
        }
        ArrayNode areasOfWorkArray = item.putArray("areasOfWork");
        for (String aow : areasOfWork) {
            areasOfWorkArray.add(aow);
        }
        ArrayNode interestsArray = item.putArray("interests");
        for (String interest : interests) {
            interestsArray.add(interest);
        }
        return this;
    }

    public JsonCourseBuilder addAudience(String[] grades, String[] areasOfWork) {
        ArrayNode aud = getOrCreateArray("audiences");
        ObjectNode item = aud.addObject();
        item.put("type", "OPEN");
        ArrayNode gradesArray = item.putArray("grades");
        for (String grade : grades) {
            gradesArray.add(grade);
        }
        ArrayNode areasOfWorkArray = item.putArray("areasOfWork");
        for (String aow : areasOfWork) {
            areasOfWorkArray.add(aow);
        }
        return this;
    }

    public JsonCourseBuilder addDepartmentRequiredLearning(String departmentCode, String requiredBy) {
        createRequiredAudience(departmentCode, requiredBy);
        return this;
    }

    public JsonCourseBuilder level(String level) {
        root.put("level", level);
        return this;
    }

    public JsonCourseBuilder status(String status) {
        root.put("status", status);
        return this;
    }

    public JsonCourseBuilder addLearningTag(Long tagId, String tagName, String tagUrl) {
        ArrayNode aud = getOrCreateArray("learningTags");
        ObjectNode item = aud.addObject();
        item.put("id", tagId)
                .put("name", tagName)
                .put("urlSlug", tagUrl);
        return this;
    }

    public JsonCourseBuilder addDepartmentRequiredLearning(String departmentCode, String requiredBy, String frequency) {
        ObjectNode audience = createRequiredAudience(departmentCode, requiredBy);
        audience.put("frequency", frequency);
        return this;
    }

    public JsonCourseBuilder addModule(ObjectNode module) {
        getOrCreateArray("modules").add(module);
        return this;
    }

    public JsonCourseBuilder addModule(String type, String id, String title, boolean optional, int duration) {
        getOrCreateArray("modules").add(JsonModuleBuilder.create(type, id, courseId, title, optional, duration).get());
        return this;
    }

    public JsonCourseBuilder addFaceToFaceModule(String id, String title, int duration,
                                                 String eventId, BigDecimal cost, DateRangeJsonValues... dateRanges) {
        addModule(JsonModuleBuilder.buildFaceToFace(id, courseId, title, duration, cost, eventId, dateRanges).get());
        return this;
    }

    public JsonCourseBuilder addLinkModule(String id, String title, boolean optional, int duration) {
        return this.addModule("link", id, title, optional, duration);
    }

    public JsonCourseBuilder addFileModule(String id, String title, boolean optional, int duration) {
        getOrCreateArray("modules").add(JsonModuleBuilder.create("file", id, courseId, title, optional, duration)
                .addUrlAndSize("location/file.txt", 1000).get());
        return this;
    }

    public JsonCourseBuilder addElearningModule(String id, String title, boolean optional, int duration) {
        return this.addModule("elearning", id, title, optional, duration);
    }
}
