package uk.gov.cabinetoffice.csl.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CslServiceController.class)
@WithMockUser
public class CslServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTest() throws Exception {
        String input = "abc";
        RequestBuilder requestBuilder = buildGet(input);
        MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
        Assertions.assertEquals(input, result.getResponse().getContentAsString());
    }

    MockHttpServletRequestBuilder buildGet(String input) {
        return get("/csl/test/" + input)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON);
    }
}

