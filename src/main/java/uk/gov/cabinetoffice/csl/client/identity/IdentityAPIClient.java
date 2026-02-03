package uk.gov.cabinetoffice.csl.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.identity.AgencyTokenCapacityUsed;
import uk.gov.cabinetoffice.csl.domain.identity.Identity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class IdentityAPIClient implements IIdentityAPIClient {

    private final IHttpClient oauthClient;
    @Value("${identity.agencyTokenCapacityUsedUrl}")
    private String agencyTokenCapacityUsed;
    @Value("${identity.identitiesUrl}")
    private String identitiesUrl;
    @Value("${identity.UidMapUrl}")
    private String uidMapUrl;

    public IdentityAPIClient(@Qualifier("identityOAuthHttpClient") IHttpClient oauthClient) {
        this.oauthClient = oauthClient;
    }

    @Override
    public AgencyTokenCapacityUsed getCapacityUsedForAgencyToken(String tokenUid) {
        String url = String.format("%s/%s", agencyTokenCapacityUsed, tokenUid);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return oauthClient.executeRequest(request, AgencyTokenCapacityUsed.class);
    }

    @Override
    public Optional<Identity> getIdentityWithEmail(String email) {
        String url = String.format("%s?emailAddress=%s", identitiesUrl, email);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return Optional.ofNullable(oauthClient.executeRequest(request, Identity.class));
    }

    @Override
    public Map<String, Identity> fetchByUids(List<String> uids) {
        String url = String.format("%s?uids=%s", uidMapUrl, String.join(",", uids));
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return oauthClient.executeMapRequest(request, new ParameterizedTypeReference<>() {
        });
    }

}
