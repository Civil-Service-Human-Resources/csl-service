package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradeListDeserializer extends JsonDeserializer<List<Grade>> {
    @Override
    public List<Grade> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);
        JsonNode gradesNode = root.get("grades");

        List<Grade> grades = new ArrayList<>();

        for (JsonNode gradeNode : gradesNode) {
            HalObject hal = mapper.treeToValue(gradeNode, HalObject.class);
            grades.add(new Grade(hal.getId(), hal.getCode(), hal.getName()));
        }

        return grades;
    }
}
