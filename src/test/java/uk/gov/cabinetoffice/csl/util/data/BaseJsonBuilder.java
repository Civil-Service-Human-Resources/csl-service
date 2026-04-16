package uk.gov.cabinetoffice.csl.util.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseJsonBuilder {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ObjectNode root;

    public BaseJsonBuilder(ObjectNode root) {
        this.root = root;
    }

    public BaseJsonBuilder(String root) throws JsonProcessingException {
        this.root = mapper.readTree(root).withObject("/");
    }

    public BaseJsonBuilder() {
        this.root = mapper.createObjectNode();
    }

    protected ArrayNode getOrCreateArray(String fieldName) {
        JsonNode node = root.get(fieldName);
        if (node instanceof ArrayNode) {
            return (ArrayNode) node;
        }
        return root.putArray(fieldName);
    }

    public ObjectNode get() {
        return root;
    }

    public String build() {
        return root.toString();
    }
}
