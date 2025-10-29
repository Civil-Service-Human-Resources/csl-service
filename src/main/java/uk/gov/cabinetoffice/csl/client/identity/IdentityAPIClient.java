package uk.gov.cabinetoffice.csl.client.identity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.identity.AgencyTokenCapacityUsed;

@Component
@Slf4j
public class IdentityAPIClient implements IIdentityAPIClient {

    private final IHttpClient oauthClient;
    @Value("${identity.agencyTokenCapacityUsedUrl}")
    private String agencyTokenCapacityUsed;

    public IdentityAPIClient(@Qualifier("identityOAuthHttpClient") IHttpClient oauthClient) {
        this.oauthClient = oauthClient;
    }

    @Override
    public AgencyTokenCapacityUsed getCapacityUsedForAgencyToken(String tokenUid) {
        String url = String.format("%s/%s", agencyTokenCapacityUsed, tokenUid);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return oauthClient.executeRequest(request, AgencyTokenCapacityUsed.class);
    }

}
