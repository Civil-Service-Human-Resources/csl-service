package uk.gov.cabinetoffice.csl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
public class CslTestUtil {

    private final ObjectMapper mapper;

    public CslTestUtil(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @SneakyThrows
    public String toJson(Object o) {
        return mapper.writeValueAsString(o);
    }

}
