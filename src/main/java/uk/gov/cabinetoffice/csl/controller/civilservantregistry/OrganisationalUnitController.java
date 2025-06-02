package uk.gov.cabinetoffice.csl.controller.civilservantregistry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.service.civilservantregistry.CivilServantRegistryService;

import java.util.List;

@Slf4j
@RestController
public class OrganisationalUnitController {

    private final CivilServantRegistryService civilServantRegistryService;

    public OrganisationalUnitController(CivilServantRegistryService civilServantRegistryService) {
        this.civilServantRegistryService = civilServantRegistryService;
    }

    @GetMapping("/v2/organisationalUnits")
    @ResponseBody
    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return civilServantRegistryService.getAllOrganisationalUnits();
    }
}
