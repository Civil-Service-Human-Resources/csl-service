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

        mockMvc.perform(get("/grades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.grades[0].id").value("1"))
                .andExpect(jsonPath("$.grades[0].code").value("AA"))
                .andExpect(jsonPath("$.grades[0].name").value("Administrative assistant"))
                .andExpect(jsonPath("$.grades[1].id").value("2"))
                .andExpect(jsonPath("$.grades[1].code").value("AO"))
                .andExpect(jsonPath("$.grades[1].name").value("Administrative officer"))
                .andExpect(jsonPath("$.grades[2].id").value("3"))
                .andExpect(jsonPath("$.grades[2].code").value("EO"))
                .andExpect(jsonPath("$.grades[2].name").value("Executive officer"))
                .andExpect(jsonPath("$.grades[3].id").value("4"))
                .andExpect(jsonPath("$.grades[3].code").value("G6"))
                .andExpect(jsonPath("$.grades[3].name").value("Grade 6"));
    }
}
