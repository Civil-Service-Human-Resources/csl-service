package uk.gov.cabinetoffice.csl.client.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.ParallelHttpClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.AgencyTokenDTO;
import uk.gov.cabinetoffice.csl.controller.csrs.model.CreateDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.DeleteDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;

import java.util.List;

@Component
@Slf4j
public class CSRSClient implements ICSRSClient {

    private final IHttpClient httpClient;
    private final CsrsConfiguration csrsConfiguration;
    private final OrganisationalUnitFactory organisationalUnitFactory;

    public CSRSClient(@Qualifier("csrsHttpClient") ParallelHttpClient httpClient, CsrsConfiguration csrsConfiguration,
                      OrganisationalUnitFactory organisationalUnitFactory) {
        this.httpClient = httpClient;
        this.organisationalUnitFactory = organisationalUnitFactory;
        this.csrsConfiguration = csrsConfiguration;
    }

    @Override
    public CivilServant getCivilServantProfileWithUid(String uid) {
        log.info("Getting civil servant with uid '{}'", uid);
        String url = csrsConfiguration.getCivilServantProfileUrl(uid);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, CivilServant.class);
    }

    @Override
    public OrganisationalUnitMap fetch() {
        log.info("Getting all organisational units from csrs");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(csrsConfiguration.getAllOrganisationalUnits());
        List<OrganisationalUnit> organisationalUnits = httpClient.getPaginatedRequest(OrganisationalUnitsPagedResponse.class, uriBuilder,
                        csrsConfiguration.getOrganisationalUnitMaxPageSize())
                .stream().toList();
        return organisationalUnitFactory.buildOrganisationalUnits(organisationalUnits);
    }

    @Override
    @Cacheable("areas-of-work")
    public List<AreaOfWork> getAreasOfWork() {
        return httpClient.executeTypeReferenceRequest(
                RequestEntity.get(csrsConfiguration.getProfessionsTree()).build(),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Override
    @Cacheable("grades")
    public List<Grade> getGrades() {
        GetGradesResponse response = httpClient.executeTypeReferenceRequest(
                RequestEntity.get(csrsConfiguration.getGrades()).build(),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getGrades();
    }

    @Override
    public void patchCivilServant(PatchCivilServantDto patch) {
        String url = csrsConfiguration.getCivilServantMeUrl();
        httpClient.executeRequest(RequestEntity.patch(url).body(patch.getAsApiParams()), Void.class);
    }

    @Override
    public void patchCivilServantOrganisation(OrganisationalUnitIdDto organisationalUnitIdDTO) {
        String url = csrsConfiguration.getCivilServantMeOrganisationUrl();
        httpClient.executeRequest(RequestEntity.patch(url).body(organisationalUnitIdDTO), Void.class);
    }

    @Override
    public void deleteOrganisationalUnit(Long organisationalUnitId) {
        String url = csrsConfiguration.getOrganisationalUnitUrl(organisationalUnitId);
        httpClient.executeRequest(RequestEntity.delete(url).build(), Void.class);
    }

    @Override
    public void patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        String url = csrsConfiguration.getOrganisationalUnitUrl(organisationalUnitId);
        log.info("Updating organisational unit data in csrs: {} for organisationalUnitId: {}", organisationalUnitDto, organisationalUnitId);
        httpClient.executeRequest(RequestEntity.patch(url).body(organisationalUnitDto), Void.class);
    }

    @Override
    public AgencyToken createAgencyToken(Long organisationalUnitId, AgencyTokenDTO agencyTokenDto) {
        String url = csrsConfiguration.getAgencyTokenUrl(organisationalUnitId);
        return httpClient.executeRequest(RequestEntity.post(url).body(agencyTokenDto), AgencyToken.class);
    }

    @Override
    public AgencyToken updateAgencyToken(Long organisationalUnitId, AgencyTokenDTO agencyTokenDto) {
        String url = csrsConfiguration.getAgencyTokenUrl(organisationalUnitId);
        return httpClient.executeRequest(RequestEntity.patch(url).body(agencyTokenDto), AgencyToken.class);
    }

    @Override
    public void deleteAgencyToken(Long organisationalUnitId) {
        String url = csrsConfiguration.getAgencyTokenUrl(organisationalUnitId);
        httpClient.executeRequest(RequestEntity.delete(url).build(), Void.class);
    }

    @Override
    public OrganisationalUnit createOrganisationalUnit(OrganisationalUnitDto organisationalUnitDto) {
        String url = csrsConfiguration.getOrganisationalUnits();
        return httpClient.executeRequest(RequestEntity.post(url).body(organisationalUnitDto), OrganisationalUnit.class);
    }

    @Override
    public UpdateDomainResponse addDomainToOrganisation(Long organisationalUnitId, CreateDomainDto domain) {
        String url = csrsConfiguration.getDomainsUrl(organisationalUnitId);
        return httpClient.executeRequest(RequestEntity.post(url).body(domain), UpdateDomainResponse.class);
    }

    @Override
    public UpdateDomainResponse deleteDomain(Long organisationalUnitId, Long domainId, DeleteDomainDto body) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(csrsConfiguration.getDomainsUrl(organisationalUnitId, domainId))
                .queryParam("includeSubOrgs", body.isIncludeSubOrgs());
        return httpClient.executeRequest(RequestEntity.delete(uriBuilder.toUriString()).build(), UpdateDomainResponse.class);
    }

}
