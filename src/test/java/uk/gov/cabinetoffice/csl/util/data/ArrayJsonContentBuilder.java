package uk.gov.cabinetoffice.csl.util.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.stream.Stream;

public class ArrayJsonContentBuilder<T extends BaseJsonBuilder> {

    protected final ObjectMapper mapper = new ObjectMapper();
    protected final ArrayNode root;

    public ArrayJsonContentBuilder() {
        this.root = mapper.createArrayNode();
    }

    public static <T extends BaseJsonBuilder> ArrayJsonContentBuilder<T> create() {
        return new ArrayJsonContentBuilder<>();
    }

    @SafeVarargs
    public static <T extends BaseJsonBuilder> ArrayJsonContentBuilder<T> create(T... elems) {
        return new ArrayJsonContentBuilder<T>().addElements(elems);
    }

    @SafeVarargs
    public final ArrayJsonContentBuilder<T> addElements(T... elems) {
        root.addAll(Stream.of(elems).map(BaseJsonBuilder::get).toList());
        return this;
    }

    public ArrayNode get() {
        return root;
    }

    public String build() {
        return root.toString();
    }

    public ObjectNode getAsPaginated(int page, int size, int totalPages) {
        ObjectNode pagedRoot = mapper.createObjectNode();
        pagedRoot.putArray("content").addAll(this.get());
        pagedRoot.put("page", page);
        pagedRoot.put("size", size);
        pagedRoot.put("totalElements", this.get().size());
        pagedRoot.put("totalPages", totalPages);
        return pagedRoot;
    }

    public ObjectNode getAsSearchResults(int page, int size, int totalPages) {
        ObjectNode pagedRoot = mapper.createObjectNode();
        pagedRoot.putArray("results").addAll(this.get());
        pagedRoot.put("page", page);
        pagedRoot.put("size", size);
        pagedRoot.put("totalResults", this.get().size());
        return pagedRoot;
    }

    public ObjectNode getAsObjectList(String key) {
        ObjectNode pagedRoot = mapper.createObjectNode();
        pagedRoot.putArray(key).addAll(this.get());
        return pagedRoot;
    }
}
