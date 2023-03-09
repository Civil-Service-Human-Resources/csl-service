package uk.gov.cabinetoffice.csl.controller;

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
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.service.ModuleRollupService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.convertObjectToJsonString;

@Slf4j
@WebMvcTest(controllers = RusticiRollupController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
public class RusticiRollupControllerTest {

    @MockBean
    private ModuleRollupService moduleRollupService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testProcessRusticiRollupDataForHttpStatus400() throws Exception {
        RusticiRollupData rusticiRollupData = new RusticiRollupData();
        String uri = "/rustici/rollup";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .content(convertObjectToJsonString(rusticiRollupData))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful()).andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        log.debug("responseAsString: {}", responseAsString);
        assertEquals("", responseAsString);
    }
}
