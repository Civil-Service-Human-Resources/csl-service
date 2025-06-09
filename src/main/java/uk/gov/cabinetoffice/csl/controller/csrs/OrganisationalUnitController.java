package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitNames;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnits;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

@Slf4j
@RestController
@RequestMapping("/organisations")
public class OrganisationalUnitController {

    private final CivilServantRegistryService civilServantRegistryService;

    public OrganisationalUnitController(CivilServantRegistryService civilServantRegistryService) {
        this.civilServantRegistryService = civilServantRegistryService;
    }

    @GetMapping(path = "/full", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public OrganisationalUnits getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return civilServantRegistryService.getAllOrganisationalUnits();

    }

    @GetMapping(path = "/formatted_list", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames() {
        log.info("Getting formatted organisational unit names");
        return civilServantRegistryService.getFormattedOrganisationalUnitNames();
    }
}
