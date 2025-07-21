package uk.gov.cabinetoffice.csl.service.report;

import org.junit.jupiter.api.Test;
import uk.gov.cabinetoffice.csl.client.reportService.IReportServiceClient;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnits;
import uk.gov.cabinetoffice.csl.service.ReportService;
import uk.gov.cabinetoffice.csl.service.chart.ChartFactoryService;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ReportServiceTest {
    @Test
    public void testGetOrganisationIdsWithChildrenAsFlatListReturnsListOfOrganisationsWithChildrenAsAFlatList(){
        OrganisationalUnitListService fakeOrganisationalUnitListService = mock(OrganisationalUnitListService.class);
        doReturn(new OrganisationalUnits(getOrganisationalUnitsWithChildren())).when(fakeOrganisationalUnitListService).getAllOrganisationalUnitsWithChildren();

        ReportService reportService = new ReportService(mock(IReportServiceClient.class), mock(ChartFactoryService.class), fakeOrganisationalUnitListService);

        List<String> result = reportService.getOrganisationIdsWithChildrenAsFlatList(Arrays.asList("1", "2", "3"));

        assertEquals(result.get(0), "1");
        assertEquals(result.get(1), "4");
        assertEquals(result.get(2), "6");
        assertEquals(result.get(3), "2");
        assertEquals(result.get(4), "3");
        assertEquals(result.get(5), "5");
    }

    private List<OrganisationalUnit> getOrganisationalUnitsWithChildren(){
        // Tree:
        // - 1
        //  |_ 4
        //     |_ 6
        // - 2
        // - 3
        //   |_5

        OrganisationalUnit org1 = new OrganisationalUnit();
        org1.setId(1L);

        OrganisationalUnit org2 = new OrganisationalUnit();
        org2.setId(2L);

        OrganisationalUnit org3 = new OrganisationalUnit();
        org3.setId(3L);

        OrganisationalUnit org4 = new OrganisationalUnit();
        org4.setId(4L);

        OrganisationalUnit org5 = new OrganisationalUnit();
        org5.setId(5L);

        OrganisationalUnit org6 = new OrganisationalUnit();
        org6.setId(6L);

        org4.setChildren(Arrays.asList(org6));

        org1.setChildren(Arrays.asList(org4));
        org3.setChildren(Arrays.asList(org5));

        return Arrays.asList(org1, org2, org3);
    }
}
