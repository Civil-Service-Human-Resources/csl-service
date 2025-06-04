package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitName;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/organisations")
public class OrganisationalUnitController {

    private final CivilServantRegistryService civilServantRegistryService;

    public OrganisationalUnitController(CivilServantRegistryService civilServantRegistryService) {
        this.civilServantRegistryService = civilServantRegistryService;
    }

    @GetMapping(path = "/full", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return civilServantRegistryService.getAllOrganisationalUnits();
    }

    @GetMapping(path = "/formatted_list", produces = "application/json")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<FormattedOrganisationalUnitName> getFormattedOrganisationalUnitNames() {
        log.info("Getting formatted organisational unit names");
        return civilServantRegistryService.getFormattedOrganisationalUnitNames();
    }
}
