package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.FormattedOrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitNames;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnits;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;

@Slf4j
@RestController
@RequestMapping("/organisations")
@AllArgsConstructor
public class OrganisationalUnitController {

    private final CivilServantRegistryService civilServantRegistryService;
    private final OrganisationalUnitListService organisationalUnitService;

    @GetMapping(path = "/full", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public OrganisationalUnits getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return organisationalUnitService.getAllOrganisationalUnitsWithChildren();

    }

    @GetMapping(path = "/formatted_list", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames(FormattedOrganisationalUnitsParams formattedOrganisationalUnitsParams) {
        log.info("Getting formatted organisational unit names");
        return civilServantRegistryService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams);
    }
}
