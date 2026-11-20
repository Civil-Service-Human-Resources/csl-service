package uk.gov.cabinetoffice.csl.util.data.catalogue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

import java.math.BigDecimal;

public class JsonModuleBuilder extends BaseJsonBuilder {

    public static JsonModuleBuilder create(String type, String id, String courseId,
                                           String title, boolean optional, int duration) {
        JsonModuleBuilder builder = new JsonModuleBuilder();
        builder.root.put("id", id);
        builder.root.put("courseId", courseId);
        builder.root.put("title", title);
        builder.root.put("description", title);
        builder.root.put("type", type);
        builder.root.put("moduleType", type);
        builder.root.put("duration", duration);
        builder.root.put("link", "https://gov.uk/");
        builder.root.put("optional", optional);
        return builder;
    }

    public static JsonModuleBuilder buildFaceToFace(String id, String courseId, String title, int duration,
                                                    BigDecimal cost, String eventId, DateRangeJsonValues... dateRanges) {
        return JsonModuleBuilder.buildFaceToFace(id, courseId, title, duration, cost, eventId, new VenueValues("London", "1 London Road", 10, 5), dateRanges);
    }

    public static JsonModuleBuilder buildFaceToFace(String id, String courseId, String title, int duration,
                                                    BigDecimal cost, String eventId, VenueValues venue, DateRangeJsonValues... dateRanges) {
        JsonModuleBuilder builder = JsonModuleBuilder.create("face-to-face", id, courseId, title, false, duration);
        builder.root.put("cost", cost);
        ArrayNode events = builder.getOrCreateArray("events");
        events.add(JsonEventBuilder.create(eventId, venue, dateRanges).get());
        return builder;
    }

    public JsonModuleBuilder addEvent(String id, DateRangeJsonValues... dateRanges) {
        ArrayNode events = getOrCreateArray("events");
        events.add(JsonEventBuilder.create(id, new VenueValues("London", "1 London Road", 10, 5), dateRanges).get());
        return this;
    }

    public JsonModuleBuilder addUrlAndSize(String url, Integer size) {
        root.put("url", url);
        root.put("fileSize", size);
        return this;
    }

}
