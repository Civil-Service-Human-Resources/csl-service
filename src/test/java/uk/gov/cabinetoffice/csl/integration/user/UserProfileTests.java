package uk.gov.cabinetoffice.csl.integration.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.service.messaging.model.Message;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserProfileTests extends IntegrationTestBase {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void testSetOtherAreasOfWorkNewProfile() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        cslStubService.getCsrsStubService().getAreasOfWork("""
                [
                    {
                        "id": 1,
                        "name": "DdaT"
                    },
                    {
                        "id": 2,
                        "name": "Finance"
                    }
                ]
                """);
        cslStubService.getCsrsStubService().patchCivilServant("""
                {
                    "otherAreasOfWork": ["/professions/1", "/professions/2"]
                }
                """);
        mockMvc.perform(post("/user/profile/other-areas-of-work?newProfile=true")
                        .content("[1,2,3]")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(jmsTemplate, atLeast(1)).convertAndSend(anyString(), any(Message.class));
    }

    @Test
    public void testUpdateFullName() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        cslStubService.getCsrsStubService().patchCivilServant("""
                {
                    "fullName": "test full Name"
                }
                """);
        mockMvc.perform(post("/user/profile/full-name")
                        .content("""
                            {
                                "fullName": "test full Name"
                            }
                            """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(jmsTemplate, atLeast(1)).convertAndSend(anyString(), any(Message.class));
    }

    @Test
    public void testUpdateGrade() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        cslStubService.getCsrsStubService().getGrades("""
                [
                    {
                        "id": 1,
                        "code": "AA",
                        "name": "Administrative assistant"
                    },
                    {
                        "id": 2,
                        "code": "AO",
                        "name": "Administrative officer"
                    },
                    {
                        "id": 3,
                        "code": "EO",
                        "name": "Executive officer"
                    },
                    {
                        "id": 4,
                        "code": "G6",
                        "name": "Grade 6"
                    }
                ]
                """);
        cslStubService.getCsrsStubService().patchCivilServant("""
                {
                    "grade": "/grades/1"
                }
                """);
        mockMvc.perform(post("/user/profile/grade")
                        .content("1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(jmsTemplate, atLeast(1)).convertAndSend(anyString(), any(Message.class));
    }
}
