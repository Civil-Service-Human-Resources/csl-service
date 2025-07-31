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
