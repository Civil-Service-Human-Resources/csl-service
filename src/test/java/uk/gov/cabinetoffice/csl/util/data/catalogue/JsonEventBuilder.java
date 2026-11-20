package uk.gov.cabinetoffice.csl.util.data.catalogue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.gov.cabinetoffice.csl.util.data.BaseJsonBuilder;

import java.util.Arrays;

public class JsonEventBuilder extends BaseJsonBuilder {

    public static JsonEventBuilder create(String id, VenueValues venue, DateRangeJsonValues... dateRanges) {
        JsonEventBuilder builder = new JsonEventBuilder();
        ArrayNode dateRangeArray = builder.root.put("id", id).putArray("dateRanges");
        Arrays.asList(dateRanges).forEach(dr -> dateRangeArray.addObject()
                .put("startTime", dr.startTime())
                .put("endTime", dr.endTime())
                .put("date", dr.date()));
        ObjectNode venueObject = builder.root.putObject("venue");
        venueObject.put("location", venue.location())
                .put("address", venue.address())
                .put("minCapacity", venue.minimumCapacity())
                .put("capacity", venue.capacity());
        return builder;
    }

}
