package uk.gov.cabinetoffice.csl.integration.csrs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GradesTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void testGetGrades() throws Exception {
        cslStubService.getCsrsStubService().getGrades("""
                {
                     "_embedded": {
                         "grades": [
                             {
                                 "code": "AA",
                                 "name": "Administrative assistant",
                                 "_links": {
                                     "self": {
                                         "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/1"
                                     },
                                     "grade": {
                                         "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/1"
                                     },
                                     "organisationalUnit": {
                                         "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/1/organisationalUnit"
                                     }
                                 }
                             },
                             {
                                 "code": "AO",
                                 "name": "Administrative officer",
                                 "_links": {
                                     "self": {
                                         "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/2"
                                     },
                                     "grade": {
                                         "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/2"
                                     },
                                     "organisationalUnit": {
                                         "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/2/organisationalUnit"
                                     }
                                 }
                             }
                         ]
                     },
                     "_links": {
                         "self": {
                             "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades"
                         },
                         "profile": {
                             "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/profile/grades"
                         },
                         "search": {
                             "href": "https://civil-servant-registry.performance.learn.civilservice.gov.uk/grades/search"
                         }
                     }
                 }
                """);

        mockMvc.perform(get("/grades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.grades[0].id").value("1"))
                .andExpect(jsonPath("$.grades[0].code").value("AA"))
                .andExpect(jsonPath("$.grades[0].name").value("Administrative assistant"))
                .andExpect(jsonPath("$.grades[1].id").value("2"))
                .andExpect(jsonPath("$.grades[1].code").value("AO"))
                .andExpect(jsonPath("$.grades[1].name").value("Administrative officer"));
    }
}
