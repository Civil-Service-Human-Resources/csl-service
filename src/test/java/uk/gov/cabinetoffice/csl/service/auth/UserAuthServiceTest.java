package uk.gov.cabinetoffice.csl.service.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.cabinetoffice.csl.domain.error.ClientAuthenticationErrorException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserAuthServiceTest {
    private final SecurityContext securityContext = mock(SecurityContext.class);
    private UserAuthService userAuthService;

    private void mockGetAuth(Jwt returnPrincipal) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(returnPrincipal);
        when(securityContext.getAuthentication()).thenReturn(auth);
    }

    @Test
    public void getBearerTokenFromUserAuth() {
        Jwt principal = mock(Jwt.class);
        mockGetAuth(principal);
        userAuthService = new UserAuthService(securityContext);
        Jwt result = userAuthService.getBearerTokenFromUserAuth();
        assertEquals(principal, result);
    }

    @Test
    public void getUsername() {
        Jwt principal = mock(Jwt.class);
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_name", "username");
        when(principal.getClaims()).thenReturn(claims);
        mockGetAuth(principal);
        userAuthService = new UserAuthService(securityContext);
        String result = userAuthService.getUsername();
        assertEquals("username", result);
    }

    @Test
    public void getUsernameThrowWhenBlank() {
        Jwt principal = mock(Jwt.class);
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_name", null);
        when(principal.getClaims()).thenReturn(claims);
        mockGetAuth(principal);
        userAuthService = new UserAuthService(securityContext);
        assertThrows(ClientAuthenticationErrorException.class, userAuthService::getUsername);
    }
}
