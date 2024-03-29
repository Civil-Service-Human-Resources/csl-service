package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.Grade;

@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final ICSRSClient csrsClient;

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    public User getUserWithUid(String uid) {
        CivilServant civilServant = csrsClient.getCivilServantProfileWithUid(uid);
        Grade grade = civilServant.getGrade();
        return new User(uid, civilServant.getEmail(), civilServant.getOrganisationalUnit().getId().intValue(),
                civilServant.getProfession().getId().intValue(), grade == null ? null : grade.getId().intValue(),
                civilServant.getDepartmentHierarchy());
    }
}
