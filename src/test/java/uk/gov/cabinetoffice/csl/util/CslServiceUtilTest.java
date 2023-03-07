package uk.gov.cabinetoffice.csl.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.cabinetoffice.csl.domain.OAuthToken;
import uk.gov.cabinetoffice.csl.service.IdentityService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CslServiceUtilTest {

    @Mock
    private IdentityService identityService;

    @MockBean
    private CslServiceUtil cslServiceUtil;

    private final long refreshServiceTokenCacheBeforeSecondsToExpire = 0;

    private Integer serviceTokenExpiryInSeconds = 80843;

    @BeforeEach
    public void setup() {
        cslServiceUtil = new CslServiceUtil(identityService, refreshServiceTokenCacheBeforeSecondsToExpire);
    }

    @Test
    public void getBearerTokenShouldReturnValidTokenAndShouldNotRefreshTheCacheWhenIdentityServiceReturnValidOAuthToken() {
        OAuthToken validOAuthToken = createValidOAuthToken();
        mockIdentityServiceGetOAuthServiceToken(validOAuthToken);
        String bearerToken = cslServiceUtil.getBearerToken();
        assertEquals(validOAuthToken.getAccessToken(), bearerToken);
        verify(identityService, times(1)).getOAuthServiceToken();
        verify(identityService, times(0)).removeServiceTokenFromCache();
    }

    @Test
    public void getBearerTokenShouldRefreshTheCacheAndReturnValidTokenWhenServiceTokenExpired() {
        OAuthToken validOAuthToken = createValidOAuthToken();
        validOAuthToken.setExpiryDate(LocalDateTime.now().minusSeconds(5));
        mockIdentityServiceGetOAuthServiceToken(validOAuthToken);
        mockIdentityServiceRemoveServiceTokenFromCache();
        String bearerToken = cslServiceUtil.getBearerToken();
        assertEquals(validOAuthToken.getAccessToken(), bearerToken);
        verify(identityService, times(2)).getOAuthServiceToken();
        verify(identityService, times(1)).removeServiceTokenFromCache();
    }

    @Test
    public void getBearerTokenShouldReturnEmptyTokenWhenIdentityServiceReturnInValidOAuthToken() {
        mockIdentityServiceGetOAuthServiceToken(new OAuthToken());
        mockIdentityServiceRemoveServiceTokenFromCache();
        String bearerToken = cslServiceUtil.getBearerToken();
        assertNull(bearerToken);
        verify(identityService, times(2)).getOAuthServiceToken();
        verify(identityService, times(1)).removeServiceTokenFromCache();
    }

    private void mockIdentityServiceGetOAuthServiceToken(OAuthToken oAuthToken) {
        when(identityService.getOAuthServiceToken()).thenReturn(oAuthToken);
    }

    private void mockIdentityServiceRemoveServiceTokenFromCache() {
        doNothing().when(identityService).removeServiceTokenFromCache();
    }

    private OAuthToken createValidOAuthToken() {
        OAuthToken authToken = new OAuthToken();
        authToken.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNjc4Mjg4MDE0LCJhdXRob3JpdGllcyI6WyJDTElFTlQiXSwianRpIjoiY" +
                "2JjMWU1MjItMmRmOS00YTk0LWJiNTEtM2ViMzgwZjEyMzY0IiwiY2xpZW50X2lkIjoiOWZiZDRhZTItMmRiMy00NGM4LTk1NDQtODh" +
                "lODAyNTViNTZlIn0.3cbrYFHNjoAJnCBHXzcyIEusV5N8z03zO5H6ek6xy-U");
        authToken.setTokenType("bearer");
        authToken.setExpiresIn(serviceTokenExpiryInSeconds);
        authToken.setScope("read write");
        authToken.setJti("cbc1e522-2df9-4a94-bb51-3eb380f12364");
        authToken.setExpiryDate(LocalDateTime.now().plusSeconds(serviceTokenExpiryInSeconds));
        return authToken;
    }
}
