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
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;

import java.util.List;

@Component
@Slf4j
public class CSRSClient implements ICSRSClient {

    @Value("${csrs.organisationalUnitMaxPageSize}")
    private Integer organisationalUnitMaxPageSize;

    @Value("${csrs.civilServants}")
    private String civilServants;

    @Value("${csrs.allOrganisationalUnits}")
    private String allOrganisationalUnits;

    @Value("${csrs.professions}")
    private String professionsTree;

    @Value("${csrs.grades}")
    private String grades;

    private final IHttpClient httpClient;

    public CSRSClient(@Qualifier("csrsHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CivilServant getCivilServantProfileWithUid(String uid) {
        log.info("Getting civil servant with uid '{}'", uid);
        String url = String.format("%s/resource/%s/profile", civilServants, uid);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeRequest(request, CivilServant.class);
    }

    @Override
    public List<OrganisationalUnit> getAllOrganisationalUnits(Boolean fetchChildren) {
        log.info("Getting all organisational units " + (fetchChildren ? "with children" : "without children"));
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(allOrganisationalUnits);
        uriBuilder.queryParam("fetchChildren", fetchChildren);
        return httpClient.getPaginatedRequest(OrganisationalUnitsPagedResponse.class, uriBuilder, organisationalUnitMaxPageSize)
                .stream().toList();
    }

    @Override
    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        return getAllOrganisationalUnits(false);
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
}
