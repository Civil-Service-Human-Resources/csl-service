package uk.gov.cabinetoffice.csl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cabinetoffice.csl.domain.ErrorResponse;
import uk.gov.cabinetoffice.csl.domain.rustici.ModuleLaunchLinkInput;
import uk.gov.cabinetoffice.csl.service.ModuleLaunchService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.convertObjectToJsonString;

@Slf4j
@WebMvcTest(controllers = ModuleLaunchController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class ModuleLaunchControllerTest {

    @MockBean
    private ModuleLaunchService moduleLaunchService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCreateModuleLaunchLinkForHttpStatus400() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";
        ModuleLaunchLinkInput moduleLaunchLinkInput = new ModuleLaunchLinkInput();
        String uri = String.format("/courses/%s/modules/%s/launch", courseId, moduleId);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .content(convertObjectToJsonString(moduleLaunchLinkInput))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isBadRequest()).andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        log.debug("responseAsString: {}", responseAsString);
        ErrorResponse errorResponse = new ObjectMapper().readValue(responseAsString, ErrorResponse.class);
        log.debug("errorResponse: {}", errorResponse);
        assertEquals(uri, errorResponse.getPath());
        assertEquals("Learner Id is missing from authentication token", errorResponse.getMessage());
    }
}
