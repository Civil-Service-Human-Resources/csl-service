package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.csrs.*;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CivilServantRegistryService {

    private final ICSRSClient civilServantRegistryClient;
    private final OrganisationalUnitListService organisationalUnitListService;

    public List<AreaOfWork> getAreasOfWork() {
        return civilServantRegistryClient.getAreasOfWork();
    }

    public List<Grade> getGrades() {
        return civilServantRegistryClient.getGrades();
    }
}
