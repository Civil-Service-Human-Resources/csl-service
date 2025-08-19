package uk.gov.cabinetoffice.csl.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.BasicOrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final ICSRSClient csrsClient;

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    public User getUserWithUid(String uid) {
        CivilServant civilServant = csrsClient.getCivilServantProfileWithUid(uid);
        List<OrganisationalUnit> departmentHierarchy = civilServant.getDepartmentHierarchy();
        ArrayList<BasicOrganisationalUnit> orgs = null;
        if (departmentHierarchy != null && !departmentHierarchy.isEmpty()) {
            orgs = departmentHierarchy
                    .stream()
                    .map(o -> new BasicOrganisationalUnit(o.getId().intValue(), o.getCode(), o.getName()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        return new User(uid,
                civilServant.getEmail(),
                civilServant.getFullName(),
                civilServant.getProfession() == null ? null : civilServant.getProfession().getId().intValue(),
                civilServant.getProfession() == null ? null : civilServant.getProfession().getName(),
                civilServant.getGrade() == null ? null : civilServant.getGrade().getId().intValue(),
                civilServant.getGrade() == null ? null : civilServant.getGrade().getName(),
                civilServant.getLineManagerName(),
                civilServant.getLineManagerEmail(),
                orgs);
    }

    @CacheEvict(value = "user", key = "#uid")
    public void removeUserFromCache(String uid) {
        log.info("User with uid {} is removed from the cache.", uid);
    }
}
