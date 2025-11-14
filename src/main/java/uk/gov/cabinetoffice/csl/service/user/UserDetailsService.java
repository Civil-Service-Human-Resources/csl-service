package uk.gov.cabinetoffice.csl.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final ICSRSClient csrsClient;

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    public User getUserWithUid(String uid) {
        CivilServant civilServant = csrsClient.getCivilServantProfileWithUid(uid);
        AreaOfWork profession = civilServant.getProfession();
        OrganisationalUnit organisationalUnit = civilServant.getOrganisationalUnit();
        Grade grade = civilServant.getGrade();
        ArrayList<BasicOrganisationalUnit> orgs = civilServant.getDepartmentHierarchy().stream().map(o -> new BasicOrganisationalUnit(o.getId().intValue(), o.getCode(), o.getName())).collect(Collectors.toCollection(ArrayList::new));
        return new User(uid,
                civilServant.getEmail(),
                civilServant.getFullName(),
                organisationalUnit == null ? null : organisationalUnit.getId(),
                profession == null ? null : profession.getId().intValue(),
                profession == null ? null : profession.getName(),
                grade == null ? null : grade.getId().intValue(),
                grade == null ? null : grade.getName(),
                civilServant.getLineManagerName(),
                civilServant.getLineManagerEmail(),
                orgs);
    }

    @CacheEvict(value = "user", key = "#uid")
    public void removeUserFromCache(String uid) {
        log.info("User with uid {} is removed from the cache.", uid);
    }
}
