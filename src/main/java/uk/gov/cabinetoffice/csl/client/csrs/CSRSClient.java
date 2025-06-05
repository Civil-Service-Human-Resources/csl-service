package uk.gov.cabinetoffice.csl.client.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.record.OrganisationalUnitsPagedResponse;

import java.util.ArrayList;
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
    public List<OrganisationalUnit> getAllOrganisationalUnits() {
        log.info("Getting all organisational units");
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(allOrganisationalUnits);
        return getPaginatedRequest(OrganisationalUnitsPagedResponse.class, uriBuilder, organisationalUnitMaxPageSize)
                .stream().toList();
    }

    private <T, R extends PagedResponse<T>> List<T> getPaginatedRequest(Class<R> pagedResponseClass, UriComponentsBuilder url, Integer maxPageSize) {
        List<T> results = new ArrayList<>();
        int totalPages = 1;
        url.queryParam("size", maxPageSize).queryParam("page", 0);
        for (int i = 0; i < totalPages; i++) {
            RequestEntity<Void> request = RequestEntity.get(url.build().toUriString()).build();
            R response = httpClient.executeRequest(request, pagedResponseClass);
            results.addAll(response.getContent());
            totalPages = response.getTotalPages();
            url.replaceQueryParam("page", i+1);
        }
        return results;
    }
}
