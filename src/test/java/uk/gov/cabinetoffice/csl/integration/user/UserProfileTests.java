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
                {
                     "_embedded": {
                         "grades": [
                             {
                                 "code": "AA",
                                 "name": "Administrative assistant",
                                 "_links": {
                                     "self": {
                                         "href": "https://hostname/grades/1"
                                     },
                                     "grade": {
                                         "href": "https://hostname/grades/1"
                                     },
                                     "organisationalUnit": {
                                         "href": "https://hostname/grades/1/organisationalUnit"
                                     }
                                 }
                             },
                             {
                                 "code": "AO",
                                 "name": "Administrative officer",
                                 "_links": {
                                     "self": {
                                         "href": "https://hostname/grades/2"
                                     },
                                     "grade": {
                                         "href": "https://hostname/grades/2"
                                     },
                                     "organisationalUnit": {
                                         "href": "https://hostname/grades/2/organisationalUnit"
                                     }
                                 }
                             }
                         ]
                     },
                     "_links": {
                         "self": {
                             "href": "https://hostname/grades"
                         },
                         "profile": {
                             "href": "https://hostname/profile/grades"
                         },
                         "search": {
                             "href": "https://hostname/grades/search"
                         }
                     }
                 }
                """);
        cslStubService.getCsrsStubService().patchCivilServant("""
                {
                    "grade": "/grades/1"
                }
                """);
        mockMvc.perform(post("/user/profile/grade")
                        .content("""
                            {
                                "gradeId": "1"
                            }
                            """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(jmsTemplate, atLeast(1)).convertAndSend(anyString(), any(Message.class));
    }

    @Test
    public void testUpdateProfession() throws Exception {
        CivilServant civilServant = testDataService.generateCivilServant();
        cslStubService.stubGetUserDetails(testDataService.getUserId(), civilServant);
        cslStubService.getCsrsStubService().getAreasOfWork("""
                [
                      {
                          "id": 1,
                          "name": "Analysis",
                          "children": []
                      },
                      {
                          "id": 2,
                          "name": "Commercial",
                          "children": [
                              {
                                  "id": 15,
                                  "name": "Strategy and Policy Development",
                                  "children": [
                                      {
                                          "id": 22,
                                          "name": "Commercial Risk and Assurance Specialist",
                                          "children": []
                                      }
                                  ]
                              }
                          ]
                      }
                  ]
                """);
        cslStubService.getCsrsStubService().patchCivilServant("""
                {
                    "profession": "/profession/2"
                }
                """);
        mockMvc.perform(post("/user/profile/profession")
                        .content("""
                            {
                                "professionId": "2"
                            }
                            """)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        verify(jmsTemplate, atLeast(1)).convertAndSend(anyString(), any(Message.class));
    }
}
