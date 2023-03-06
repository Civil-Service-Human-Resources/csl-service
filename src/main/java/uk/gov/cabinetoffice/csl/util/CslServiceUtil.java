package uk.gov.cabinetoffice.csl.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
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
import uk.gov.cabinetoffice.csl.domain.*;
import org.apache.hc.client5.http.classic.HttpClient;
import uk.gov.cabinetoffice.csl.service.IdentityService;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CslServiceUtil {

    private final IdentityService identityService;

    public CslServiceUtil(IdentityService identityService) {
        this.identityService = identityService;
    }

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

    public String getBearerToken() {
        String bearerToken = getBearerTokenFromSecurityContext();
        if(StringUtils.isBlank(bearerToken)) {
            bearerToken = getServiceTokenFromIdentityService();
        }
        return bearerToken;
    }

    public String getServiceTokenFromIdentityService() {
        //TODO: Implement the cache as below:
        //1. Get the service token from cache
        //2. if not present in cache then get it from the identity-service
        //3. and put it in cache
        //4. If token present in cache then check its expiry
        //5. If it expired or expiring in less than 5 seconds (configurable)
        //6. then get it from the identity-service and update it in the cache
        OAuthToken oAuthServiceToken = identityService.getOAuthServiceToken();
        return oAuthServiceToken != null ? oAuthServiceToken.getAccessToken() : null;
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

    public static ModuleRecord processCourseAndModuleData(LearnerRecordService learnerRecordService,
                                                          CourseRecordInput courseRecordInput) {
        ModuleRecord moduleRecord = null;
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();
        ModuleRecordInput moduleRecordInput = courseRecordInput.getModuleRecords().get(0);
        String moduleId = moduleRecordInput.getModuleId();
        ResponseEntity<?> courseRecordResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);
        if(courseRecordResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords =
                    mapJsonStringToObject((String)courseRecordResponse.getBody(), CourseRecords.class);
            log.debug("courseRecords: {}", courseRecords);
            if(courseRecords != null) {
                CourseRecord courseRecord = courseRecords.getCourseRecord(courseId);
                if(courseRecord == null) {
                    //If the course record is not present then create the course record along with module record
                    courseRecord = learnerRecordService.createInProgressCourseRecordWithModuleRecord(courseRecordInput);
                }
                if(courseRecord != null) {
                    if(courseRecord.getState() == null || courseRecord.getState().equals(State.ARCHIVED)) {
                        //Update the course record status if it is null or ARCHIVED
                        courseRecord = learnerRecordService.updateCourseRecordState(learnerId, courseId,
                                State.IN_PROGRESS);
                    }
                    //Retrieve the relevant module record from the course record
                    moduleRecord = courseRecord != null ? courseRecord.getModuleRecord(moduleId) : null;
                    if(courseRecord != null && moduleRecord == null) {
                        //If the relevant module record is not present then create the module record
                        moduleRecord = learnerRecordService.createInProgressModuleRecord(moduleRecordInput);
                    }
                    if(moduleRecord != null) {
                        if(StringUtils.isBlank(moduleRecord.getUid())) {
                            //If the uid is not present then update the module record to assign the uid
                            moduleRecord = learnerRecordService
                                    .updateModuleRecordToAssignUid(moduleRecord, learnerId, courseId);
                        }
                    }
                }
            }
        } else {
            log.error("Unable to retrieve course record for learner id: {} and course id: {}. " +
                    "Error response from learnerRecordService: {}", learnerId, courseId, courseRecordResponse);
        }
        return moduleRecord;
    }
}
