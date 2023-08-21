package uk.gov.cabinetoffice.csl.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.identity.OAuthToken;
import uk.gov.cabinetoffice.csl.service.IdentityService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.micrometer.common.util.StringUtils.isBlank;

@Service
@Slf4j
public class BearerTokenService implements IBearerTokenService {

    private final IdentityService identityService;
    private final IUserAuthService userAuthService;
    @Value("${oauth.refresh.serviceTokenCache.beforeSecondsToExpire}")
    private long refreshServiceTokenCacheBeforeSecondsToExpire;

    public BearerTokenService(IdentityService identityService, IUserAuthService userAuthService) {
        this.identityService = identityService;
        this.userAuthService = userAuthService;
    }

    private String getBearerTokenFromUserAuth() {
        Object principal = userAuthService.getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwtPrincipal) {
            return jwtPrincipal.getTokenValue();
        } else {
            return null;
        }
    }

    public String getBearerToken() {
        String bearerToken = getBearerTokenFromUserAuth();
        if (isBlank(bearerToken)) {
            OAuthToken serviceToken = identityService.getCachedOAuthServiceToken();
            log.debug("serviceToken: expiryDateTime: {}", serviceToken.getExpiryDateTime());
            long secondsRemainingToExpire = serviceToken.getExpiryDateTime() != null ?
                    ChronoUnit.SECONDS.between(LocalDateTime.now(), serviceToken.getExpiryDateTime()) : 0;
            log.debug("serviceToken: seconds remaining to service token expiry: {}", secondsRemainingToExpire);
            log.debug("serviceToken: seconds remaining to refresh the service token cache: {}",
                    (secondsRemainingToExpire - refreshServiceTokenCacheBeforeSecondsToExpire));
            if (secondsRemainingToExpire <= refreshServiceTokenCacheBeforeSecondsToExpire) {
                identityService.removeServiceTokenFromCache();
                serviceToken = identityService.getCachedOAuthServiceToken();
            }
            bearerToken = serviceToken.getAccessToken();
        }
        return bearerToken;
    }
}
