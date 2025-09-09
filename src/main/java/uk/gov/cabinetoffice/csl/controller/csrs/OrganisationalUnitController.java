package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitsParams;
import uk.gov.cabinetoffice.csl.domain.csrs.FormattedOrganisationalUnitNames;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnits;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;

@Slf4j
@RestController
@RequestMapping("/organisations")
@AllArgsConstructor
public class OrganisationalUnitController {

    private final OrganisationalUnitService organisationalUnitService;

    @GetMapping(path = "/full", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public OrganisationalUnits getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        return organisationalUnitService.getAllOrganisationalUnits();

    }

    @GetMapping(path = "/formatted_list", produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public FormattedOrganisationalUnitNames getFormattedOrganisationalUnitNames(OrganisationalUnitsParams formattedOrganisationalUnitsParams) {
        log.info("Getting formatted organisational unit names");
        return organisationalUnitService.getFormattedOrganisationalUnitNames(formattedOrganisationalUnitsParams);
    }

    @DeleteMapping("/{organisationUnitId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrganisationalUnit(@PathVariable("organisationUnitId") Long organisationUnitId) {
        log.info("Deleting organisational unit id: {}", organisationUnitId);
        organisationalUnitService.deleteOrganisationalUnit(organisationUnitId);
    }
}
