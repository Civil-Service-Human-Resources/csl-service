package uk.gov.cabinetoffice.csl.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.gov.cabinetoffice.csl.domain.identity.IdentityDto;

public interface IUserAuthService {

    Authentication getAuthentication();

    String getUsername();

    IdentityDto getIdentity();

    Jwt getBearerTokenFromUserAuth();
}
