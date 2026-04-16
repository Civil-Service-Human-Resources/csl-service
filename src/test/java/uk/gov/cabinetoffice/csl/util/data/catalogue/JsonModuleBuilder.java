package uk.gov.cabinetoffice.csl.util.data.catalogue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

import java.math.BigDecimal;
import java.util.Arrays;

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

    public JsonModuleBuilder addEvent(String id, BigDecimal cost, DateRangeJsonValues... dateRanges) {
        root.put("cost", cost);
        ArrayNode events = getOrCreateArray("events");
        ArrayNode dateRangeArray = events.addObject().put("id", id).putArray("dateRanges");
        Arrays.asList(dateRanges).forEach(dr -> dateRangeArray.addObject()
                .put("startTime", dr.startTime())
                .put("endTime", dr.endTime())
                .put("date", dr.date()));
        return this;
    }


}
