package uk.gov.cabinetoffice.csl.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.client5.http.classic.HttpClient;
import uk.gov.cabinetoffice.csl.domain.error.ErrorResponse;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;
import uk.gov.cabinetoffice.csl.service.IdentityService;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Component
public class CslServiceUtil {

    private static IdentityService identityService;

    private static long refreshServiceTokenCacheBeforeSecondsToExpire;

    public CslServiceUtil(IdentityService identityService,
                          @Value("${oauth.refresh.serviceTokenCache.beforeSecondsToExpire}")
                          long refreshServiceTokenCacheBeforeSecondsToExpire) {
        CslServiceUtil.identityService = identityService;
        CslServiceUtil.refreshServiceTokenCacheBeforeSecondsToExpire = refreshServiceTokenCacheBeforeSecondsToExpire;
    }

    public static ResponseEntity<?> returnError(HttpStatusCodeException ex, String path) {
        ErrorResponse errorResponse;
        try {
            errorResponse = ex.getResponseBodyAs(ErrorResponse.class);
        } catch(Exception e) {
            errorResponse = new ErrorResponse();
        }
        if(errorResponse == null) {
            errorResponse = new ErrorResponse();
        }
        if(isBlank(errorResponse.getMessage())) {
            errorResponse.setMessage(ex.getResponseBodyAsString());
        }
        if(isBlank(errorResponse.getError())) {
            errorResponse.setError(ex.getStatusText());
        }
        errorResponse.setStatus(String.valueOf(ex.getStatusCode().value()));
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

    public static ResponseEntity<?> createInternalServerErrorResponse() {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static Map<String, String> addAdditionalHeaderParams(String key, String value) {
        Map<String, String> additionalHeaderParams = new HashMap<>();
        additionalHeaderParams.put(key, value);
        return additionalHeaderParams;
    }

    public static String getLearnerIdFromSecurityContext() {
        Jwt jwtPrincipal = getJwtPrincipalFromSecurityContext();
        if(jwtPrincipal != null) {
            return (String)jwtPrincipal.getClaims().get("user_name");
        }
        return null;
    }

    public static String getBearerToken() {
        String bearerToken = getBearerTokenFromSecurityContext();
        if(isBlank(bearerToken)) {
            OAuthToken serviceToken = identityService.getCachedOAuthServiceToken();
            log.debug("serviceToken: expiryDateTime: {}", serviceToken.getExpiryDateTime());
            long secondsRemainingToExpire = serviceToken.getExpiryDateTime() != null ?
                    ChronoUnit.SECONDS.between(LocalDateTime.now(), serviceToken.getExpiryDateTime()) : 0;
            log.debug("serviceToken: seconds remaining to service token expiry: {}", secondsRemainingToExpire);
            log.debug("serviceToken: seconds remaining to refresh the service token cache: {}",
                    (secondsRemainingToExpire - refreshServiceTokenCacheBeforeSecondsToExpire));
            if(secondsRemainingToExpire <= refreshServiceTokenCacheBeforeSecondsToExpire) {
                identityService.removeServiceTokenFromCache();
                serviceToken = identityService.getCachedOAuthServiceToken();
            }
            bearerToken = serviceToken.getAccessToken();
        }
        return bearerToken;
    }

    public static String getBearerTokenFromSecurityContext() {
        Jwt jwtPrincipal = getJwtPrincipalFromSecurityContext();
        if(jwtPrincipal != null) {
            return jwtPrincipal.getTokenValue();
        }
        return null;
    }

    public static Jwt getJwtPrincipalFromSecurityContext() {
        Authentication authentication = getAuthenticationFromSecurityContext();
        if(authentication != null && authentication.getPrincipal() instanceof Jwt jwtPrincipal) {
            return jwtPrincipal;
        }
        return null;
    }

    public static Authentication getAuthenticationFromSecurityContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    public static ResponseEntity<?> invokeService(RequestEntity<?> requestEntity) {
        ResponseEntity<?> response;
        try {
            response = restTemplate().exchange(requestEntity, String.class);
        } catch (HttpStatusCodeException ex) {
            response = returnError(ex, requestEntity.getUrl().getPath());
        }
        return response;
    }

    public static String convertObjectToJsonString(final Object obj) {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator jsonGenerator = new JsonFactory().createGenerator(writer);
            jsonGenerator.setCodec(objectMapper());
            jsonGenerator.writeObject(obj);
            jsonGenerator.close();
            return writer.toString();
        } catch (IOException e) {
            log.error("Could not convert the Object: {}, into json String, due to: {}", obj.toString(), e.toString());
        }
        return null;
    }

    public static <T> T mapJsonStringToObject(String jsonString, Class<T> objectType) {
        try {
            return objectMapper().readValue(jsonString, objectType);
        } catch (JsonProcessingException e) {
            log.error("Could not convert the json String: {}, into Object type: {}, due to: {}",
                    jsonString, objectType.getName(), e.toString());
        }
        return null;
    }

    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
