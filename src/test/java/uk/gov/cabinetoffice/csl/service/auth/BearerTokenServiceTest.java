package uk.gov.cabinetoffice.csl.service.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;
import uk.gov.cabinetoffice.csl.service.IdentityService;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BearerTokenServiceTest {

    private IdentityService identityService = mock(IdentityService.class);
    private IUserAuthService userAuthService = mock(IUserAuthService.class);
    private Jwt jwt = mock(Jwt.class);
    private BearerTokenService bearerTokenService;

    @Test
    public void getBearerToken() {
        when(jwt.getTokenValue()).thenReturn("token");
        when(userAuthService.getBearerTokenFromUserAuth()).thenReturn(jwt);
        bearerTokenService = new BearerTokenService(null, null, userAuthService);
        String result = bearerTokenService.getBearerToken();
        assertEquals("token", result);
    }

    @Test
    public void getCachedBearerToken() {
        when(jwt.getTokenValue()).thenReturn("");
        when(userAuthService.getBearerTokenFromUserAuth()).thenReturn(jwt);
        OAuthToken token = new OAuthToken();
        token.setExpiryDateTime(LocalDateTime.of(2023, 1, 1, 10, 0, 0));
        when(identityService.getCachedOAuthServiceToken()).thenReturn(token);
        token.setAccessToken("access_token");
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
        bearerTokenService = new BearerTokenService(clock, identityService, userAuthService);
        String result = bearerTokenService.getBearerToken();
        assertEquals("access_token", result);
    }

    @Test
    public void getCachedBearerTokenWhenExpired() {
        when(jwt.getTokenValue()).thenReturn("");
        when(userAuthService.getBearerTokenFromUserAuth()).thenReturn(jwt);
        OAuthToken token = new OAuthToken();
        token.setExpiryDateTime(LocalDateTime.of(2023, 1, 1, 9, 0, 0));
        when(identityService.getCachedOAuthServiceToken()).thenReturn(token);
        token.setAccessToken("access_token");
        Clock clock = Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London"));
        bearerTokenService = new BearerTokenService(clock, identityService, userAuthService);
        String result = bearerTokenService.getBearerToken();
        assertEquals("access_token", result);
        verify(identityService, atMostOnce()).removeServiceTokenFromCache();
        verify(identityService, atLeastOnce()).removeServiceTokenFromCache();
    }
}
