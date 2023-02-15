package uk.gov.cabinetoffice.csl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.ErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CslServiceUtil {

    public static ResponseEntity<?> returnError(HttpStatusCodeException ex, String path) {
        ErrorResponse errorResponse = ex.getResponseBodyAs(ErrorResponse.class);
        if(errorResponse == null) {
            errorResponse = new ErrorResponse();
            errorResponse.setMessage(ex.getResponseBodyAsString());
        }
        errorResponse.setStatus(String.valueOf(ex.getStatusCode().value()));
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setPath(path);
        log.debug("errorResponse.getMessage(): {}", errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    public static  ResponseEntity<?> returnError(HttpStatusCode httpStatusCode, String errorMessage, String path) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), String.valueOf(httpStatusCode.value()),
                errorMessage, path);
        return new ResponseEntity<>(errorResponse, httpStatusCode);
    }

    public static Map<String, String> addAdditionalHeaderParams(String key, String value) {
        Map<String, String> additionalHeaderParams = new HashMap<>();
        additionalHeaderParams.put(key, value);
        return additionalHeaderParams;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
