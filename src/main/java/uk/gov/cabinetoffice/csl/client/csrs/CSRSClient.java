package uk.gov.cabinetoffice.csl.client.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.ParallelHttpClient;
import uk.gov.cabinetoffice.csl.controller.csrs.model.CreateDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.DeleteDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Slf4j
public class CSRSClient implements ICSRSClient {

    @Value("${csrs.organisationalUnitMaxPageSize}")
    private Integer organisationalUnitMaxPageSize;

    @Value("${csrs.civilServants}")
    private String civilServants;

    @Value("${csrs.allOrganisationalUnits}")
    private String allOrganisationalUnits;

    @Value("${csrs.organisationalUnits}")
    private String organisationalUnits;

    @Value("${csrs.professions}")
    private String professionsTree;

    @Value("${csrs.grades}")
    private String grades;

    @Value("${csrs.serviceUrl}")
    private String serviceUrl;

    private final IHttpClient httpClient;
    private final OrganisationalUnitFactory organisationalUnitFactory;

    public CSRSClient(@Qualifier("csrsHttpClient") ParallelHttpClient httpClient, OrganisationalUnitFactory organisationalUnitFactory) {
        this.httpClient = httpClient;
        this.organisationalUnitFactory = organisationalUnitFactory;
    }

    @Override
    public CivilServant getCivilServantProfileWithUid(String uid) {
        log.info("Getting civil servant with uid '{}'", uid);
        String url = String.format("%s/resource/%s/profile", civilServants, uid);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, CivilServant.class);
    }

    @Override
    public OrganisationalUnitMap getAllOrganisationalUnits() {
        log.info("Getting all organisational units from csrs");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(allOrganisationalUnits);
        List<OrganisationalUnit> organisationalUnits = httpClient.getPaginatedRequest(OrganisationalUnitsPagedResponse.class, uriBuilder, organisationalUnitMaxPageSize)
                .stream().toList();
        return organisationalUnitFactory.buildOrganisationalUnits(organisationalUnits);
    }

    @Override
    @Cacheable("areas-of-work")
    public List<AreaOfWork> getAreasOfWork() {
        return httpClient.executeTypeReferenceRequest(
                RequestEntity.get(professionsTree).build(),
                new ParameterizedTypeReference<>() {
                }
        );
    }

    @Override
    @Cacheable("grades")
    public List<Grade> getGrades() {
        GetGradesResponse response = httpClient.executeTypeReferenceRequest(
                RequestEntity.get(grades).build(),
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getGrades();
    }

    @Override
    public void patchCivilServant(PatchCivilServantDto patch) {
        String url = String.format("%s/me", civilServants);
        httpClient.executeRequest(RequestEntity.patch(url).body(patch.getAsApiParams()), Void.class);
    }

    @Override
    public void patchCivilServantOrganisation(OrganisationalUnitIdDto organisationalUnitIdDTO) {
        String url = String.format("%s/me/organisationalUnit", civilServants);
        httpClient.executeRequest(RequestEntity.patch(url).body(organisationalUnitIdDTO), Void.class);
    }

    @Override
    public void deleteOrganisationalUnit(Long organisationalUnitId) {
        String url = String.format("%s/%s", organisationalUnits, organisationalUnitId);
        httpClient.executeRequest(RequestEntity.delete(url).build(), Void.class);
    }

    @Override
    public void patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto) {
        String url = String.format("%s/%s", organisationalUnits, organisationalUnitId);
        String parent = isNotBlank(organisationalUnitDto.getParent()) ?
                serviceUrl + "/organisationalUnits/" + organisationalUnitDto.getParent() : null;
        OrganisationalUnitDto request = new OrganisationalUnitDto(organisationalUnitDto.getCode(),
                organisationalUnitDto.getName(), organisationalUnitDto.getAbbreviation(), parent);
        log.info("Updating organisational unit data in csrs: {} for organisationalUnitId: {}", request, organisationalUnitId);
        httpClient.executeRequest(RequestEntity.patch(url).body(request), Void.class);
    }

    @Override
    public UpdateDomainResponse addDomainToOrganisation(Long organisationalUnitId, CreateDomainDto domain) {
        String url = String.format("%s/%s/domains", organisationalUnits, organisationalUnitId);
        return httpClient.executeRequest(RequestEntity.post(url).body(domain), UpdateDomainResponse.class);
    }

    @Override
    public UpdateDomainResponse deleteDomain(Long organisationUnitId, Long domainId, DeleteDomainDto body) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(String.format("%s/%s/domains/%s", organisationalUnits, organisationUnitId, domainId))
                .queryParam("includeSubOrganisations", body.isIncludeSubOrganisations());
        return httpClient.executeRequest(RequestEntity.delete(uriBuilder.toUriString()).build(), UpdateDomainResponse.class);
    }
}
