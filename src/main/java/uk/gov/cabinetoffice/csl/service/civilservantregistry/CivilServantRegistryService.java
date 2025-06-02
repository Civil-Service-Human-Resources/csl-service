package uk.gov.cabinetoffice.csl.service.civilservantregistry;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.civilservantregistry.ICivilServantRegistryClient;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CivilServantRegistryService {

    private final ICivilServantRegistryClient civilServantRegistryClient;

    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return civilServantRegistryClient.getAllOrganisationalUnits();
    }
}
