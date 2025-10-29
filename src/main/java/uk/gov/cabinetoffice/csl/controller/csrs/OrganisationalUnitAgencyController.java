package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitOverview;
import uk.gov.cabinetoffice.csl.service.csrs.AgencyTokenService;

@Slf4j
@RestController
@RequestMapping("/organisations/{id}/agency-token")
@AllArgsConstructor
public class OrganisationalUnitAgencyController {

    private final AgencyTokenService agencyTokenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrganisationalUnitOverview createAgencyToken(@PathVariable("id") Long organisationUnitId,
                                                        @RequestBody AgencyTokenDTO agencyToken) {
        log.info("Adding agency token to organisational unit id: {}", organisationUnitId);
        return agencyTokenService.createAgencyToken(organisationUnitId, agencyToken);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public OrganisationalUnitOverview updateAgencyToken(@PathVariable("id") Long organisationUnitId,
                                                        @RequestBody AgencyTokenDTO agencyToken) {
        log.info("Updating agency token for organisational unit id: {}", organisationUnitId);
        return agencyTokenService.updateAgencyToken(organisationUnitId, agencyToken);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAgencyToken(@PathVariable("id") Long organisationUnitId) {
        log.info("Removing agency token from organisational unit id: {}", organisationUnitId);
        agencyTokenService.deleteAgencyToken(organisationUnitId);
    }

}
