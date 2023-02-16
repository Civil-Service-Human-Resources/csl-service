package uk.gov.cabinetoffice.csl.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
        if(StringUtils.isBlank(errorResponse.getError())) {
            errorResponse.setError(ex.getStatusText());
        }
        errorResponse.setTimestamp(LocalDateTime.now().toString());
        errorResponse.setPath(path);
        log.debug("errorResponse.getMessage(): {}", errorResponse.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    public static  ResponseEntity<?> returnError(HttpStatusCode httpStatusCode, String error, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now().toString(), String.valueOf(httpStatusCode.value()),
                error, message, path, null);
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
        if(authentication != null) {
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            learnerId = (String)jwtPrincipal.getClaims().get("user_name");

            log.debug("Authenticated?: {}", authentication.isAuthenticated());
            log.debug("Authentication jwtPrincipal: {}", jwtPrincipal);
            log.debug("Authentication jwtPrincipal Claims: {}", jwtPrincipal.getClaims());
            log.debug("Authentication jwtPrincipal Headers: {}",  jwtPrincipal.getHeaders());
            log.debug("Authentication jwtPrincipal ExpiresAt: {}", jwtPrincipal.getExpiresAt());
            log.debug("Authentication jwtPrincipal Id: {}", jwtPrincipal.getId());
            log.debug("Authentication jwtPrincipal IssuedAt: {}", jwtPrincipal.getIssuedAt());
            log.debug("Authentication jwtPrincipal TokenValue: {}", jwtPrincipal.getTokenValue());
        }
        log.debug("Learner Id from authentication token: {}", learnerId);
        return learnerId;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
