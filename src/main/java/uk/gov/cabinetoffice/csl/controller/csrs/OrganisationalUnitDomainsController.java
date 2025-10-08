package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.DomainResponse;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;

@Slf4j
@RestController
@RequestMapping("/organisations/{id}/domains")
@AllArgsConstructor
public class OrganisationalUnitDomainsController {

    private final OrganisationalUnitService organisationalUnitService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public DomainResponse addDomainToOrganisationalUnit(@PathVariable("id") Long organisationUnitId,
                                                        @RequestBody String domain) {
        log.info("Adding domain {} to organisational unit id: {}", domain, organisationUnitId);
        return organisationalUnitService.addDomainToOrganisationalUnit(organisationUnitId, domain);
    }

    @DeleteMapping("/{domainId}")
    @ResponseStatus(HttpStatus.OK)
    public DomainResponse deleteDomain(@PathVariable("id") Long organisationUnitId, @PathVariable("domainId") Long domainId,
                                       boolean cascade) {
        log.info("Removing domain with id: {} from organisational unit id: {}", domainId, organisationUnitId);
        return organisationalUnitService.removeDomainFromOrganisationalUnit(organisationUnitId, domainId, cascade);
    }

}
