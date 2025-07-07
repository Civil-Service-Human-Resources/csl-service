package uk.gov.cabinetoffice.csl.integration.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserAccountTests extends IntegrationTestBase {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void testActivateUserAccount() throws Exception {
        mockMvc.perform(post("/user/uid/activate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(jmsTemplate, atLeast(1)).convertAndSend(anyString(), any(Message.class));
    }
}
