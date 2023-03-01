package uk.gov.cabinetoffice.csl.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.gov.cabinetoffice.csl.domain.ErrorResponse;
import org.apache.hc.client5.http.classic.HttpClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CslServiceUtil {

    public static ResponseEntity<?> returnError(HttpStatusCodeException ex, String path) {
        ErrorResponse errorResponse;
        try {
            errorResponse = ex.getResponseBodyAs(ErrorResponse.class);
        } catch(Exception e) {
            errorResponse = new ErrorResponse();
            errorResponse.setMessage(ex.getResponseBodyAsString());
        }
        assert errorResponse != null;
        errorResponse.setStatus(String.valueOf(ex.getStatusCode().value()));
        if(StringUtils.isBlank(errorResponse.getError())) {
            errorResponse.setError(ex.getStatusText());
        }
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(path);
        log.error("Error received from the backend system: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    public static  ResponseEntity<?> returnError(HttpStatusCode httpStatusCode, String error, String message, String path, String[] messages) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now().toString(), String.valueOf(httpStatusCode.value()),
                error, message, path, messages);
        log.error("Returning error: {}", errorResponse);
        return new ResponseEntity<>(errorResponse, httpStatusCode);
    }

    public static Map<String, String> addAdditionalHeaderParams(String key, String value) {
        Map<String, String> additionalHeaderParams = new HashMap<>();
        additionalHeaderParams.put(key, value);
        return additionalHeaderParams;
    }

    public static String getLearnerIdFromAuth(Authentication authentication) {
        log.debug("Authentication: {}", authentication);
        String learnerId = null;
        if(authentication != null && authentication.getPrincipal() instanceof Jwt jwtPrincipal) {
            learnerId = (String)jwtPrincipal.getClaims().get("user_name");
        }
        log.debug("Learner Id from authentication token: {}", learnerId);
        return learnerId;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }
}
