package uk.gov.cabinetoffice.csl.service.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.cabinetoffice.csl.domain.error.ClientAuthenticationErrorException;

public class UserAuthService implements IUserAuthService {

    private final SecurityContext securityContext;

    public UserAuthService(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public Authentication getAuthentication() {
        return this.securityContext.getAuthentication();
    }

    @Override
    public Jwt getBearerTokenFromUserAuth() {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwtPrincipal) {
            return jwtPrincipal;
        } else {
            return null;
        }
    }

    @Override
    public String getUsername() {
        String username = "";
        Jwt jwtPrincipal = getBearerTokenFromUserAuth();
        if (jwtPrincipal != null) {
            username = (String) jwtPrincipal.getClaims().get("user_name");
        }
        if (StringUtils.isBlank(username)) {
            throw new ClientAuthenticationErrorException("Learner Id is missing from authentication token");
        }
        return username;
    }
}
