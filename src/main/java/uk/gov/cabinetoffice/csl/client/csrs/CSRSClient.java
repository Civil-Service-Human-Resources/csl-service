package uk.gov.cabinetoffice.csl.client.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.List;

@Component
@Slf4j
public class CSRSClient implements ICSRSClient {

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
        RequestEntity<Void> request = RequestEntity.get(allOrganisationalUnits).build();
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }
}
