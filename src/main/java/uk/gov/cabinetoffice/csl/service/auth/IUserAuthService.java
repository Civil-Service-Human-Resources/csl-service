package uk.gov.cabinetoffice.csl.service.auth;

import org.springframework.security.core.Authentication;

public interface IUserAuthService {

    Authentication getAuthentication();

    String getUsername();
}
