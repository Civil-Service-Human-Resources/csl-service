package uk.gov.cabinetoffice.csl.service.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.error.ClientAuthenticationErrorException;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;

import java.util.List;

@Component
public class UserAuthService implements IUserAuthService {

    private final SecurityContextService securityContextService;

    public UserAuthService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }

    public Authentication getAuthentication() {
        return securityContextService.getSecurityContext().getAuthentication();
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

    @Override
    public IdentityDto getIdentity() {
        Authentication auth = getAuthentication();
        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Jwt jwt = (Jwt) getAuthentication().getPrincipal();
        String uid = (String) jwt.getClaims().get("user_name");
        return new IdentityDto(uid, roles);
    }
}
