package uk.gov.cabinetoffice.csl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class CslTestUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public CslTestUtil() {
    }

    @SneakyThrows
    public static String toJson(Object o) {
        return mapper.writeValueAsString(o);
    }

}
