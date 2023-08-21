package uk.gov.cabinetoffice.csl.service.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.domain.error.ClientAuthenticationErrorException;

@Component
public class UserAuthService implements IUserAuthService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getUsername() {
        Authentication auth = getAuthentication();
        String username = auth.getName();
        if (StringUtils.isBlank(username)) {
            throw new ClientAuthenticationErrorException("Learner Id is missing from authentication token");
        }
        return username;
    }
}
