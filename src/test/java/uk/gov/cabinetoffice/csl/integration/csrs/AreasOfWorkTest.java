package uk.gov.cabinetoffice.csl.integration.csrs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.integration.IntegrationTestBase;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AreasOfWorkTest extends IntegrationTestBase {

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void testGetAreasOfWork() throws Exception {
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

        mockMvc.perform(get("/areas-of-work")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.areasOfWork[0].id").value("1"))
                .andExpect(jsonPath("$.areasOfWork[0].name").value("DdaT"))
                .andExpect(jsonPath("$.areasOfWork[1].id").value("2"))
                .andExpect(jsonPath("$.areasOfWork[1].name").value("Finance"));

    }

}
