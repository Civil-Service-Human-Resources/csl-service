package uk.gov.cabinetoffice.csl.client.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;

@Service
@Slf4j
public class OrganisationalUnitClient implements IOrganisationalUnitClient {

    private final ICSRSClient client;

    public OrganisationalUnitClient(ICSRSClient client) {
        this.client = client;
    }

    @Override
    public OrganisationalUnit create(OrganisationalUnitDto dto) {
        return client.createOrganisationalUnit(dto);
    }

    @Override
    public OrganisationalUnitMap fetch() {
        return OrganisationalUnitMap.buildFromList(client.getAllOrganisationalUnits());
    }
}
