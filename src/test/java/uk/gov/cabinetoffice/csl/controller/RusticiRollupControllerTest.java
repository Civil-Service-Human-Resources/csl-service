package uk.gov.cabinetoffice.csl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cabinetoffice.csl.domain.rustici.Course;
import uk.gov.cabinetoffice.csl.domain.rustici.Learner;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;
import uk.gov.cabinetoffice.csl.service.ModuleRollupService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = RusticiRollupController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("no-security")
public class RusticiRollupControllerTest {

    @MockBean
    private ModuleRollupService moduleRollupService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testServiceNotCalledIfInvalidRollup() throws Exception {
        RusticiRollupData rusticiRollupData = new RusticiRollupData();
        String uri = "/rustici/rollup";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(rusticiRollupData))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful()).andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        log.debug("responseAsString: {}", responseAsString);
        assertEquals("", responseAsString);
        verify(moduleRollupService, never()).processRusticiRollupData(rusticiRollupData);
    }

    @Test
    public void testServiceNotCalledIfInvalidCourseId() throws Exception {
        RusticiRollupData rusticiRollupData = new RusticiRollupData();
        Course course = new Course();
        course.setId("invalidId");
        Learner learner = new Learner();
        learner.setId("learnerId");
        rusticiRollupData.setCourse(course);
        rusticiRollupData.setLearner(learner);
        String uri = "/rustici/rollup";
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(uri)
                .content(mapper.writeValueAsString(rusticiRollupData))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().is2xxSuccessful()).andReturn();
        String responseAsString = result.getResponse().getContentAsString();
        log.debug("responseAsString: {}", responseAsString);
        assertEquals("", responseAsString);
        verify(moduleRollupService, never()).processRusticiRollupData(rusticiRollupData);
    }

}
