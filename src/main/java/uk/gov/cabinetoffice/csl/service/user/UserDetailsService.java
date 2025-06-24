package uk.gov.cabinetoffice.csl.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.BasicOrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.Grade;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final ICSRSClient csrsClient;

    @Cacheable(value = "user", key = "#uid", unless = "#result == null")
    public User getUserWithUid(String uid) {
        CivilServant civilServant = csrsClient.getCivilServantProfileWithUid(uid);
        Grade grade = civilServant.getGrade();
        ArrayList<BasicOrganisationalUnit> orgs = civilServant.getDepartmentHierarchy().stream().map(o -> new BasicOrganisationalUnit(o.getId().intValue(), o.getCode(), o.getName())).collect(Collectors.toCollection(ArrayList::new));
        return new User(uid,
                civilServant.getEmail(),
                civilServant.getFullName(),
                civilServant.getProfession().getId().intValue(),
                civilServant.getProfession().getName(),
                grade == null ? null : grade.getId().intValue(),
                grade == null ? null : grade.getName(),
                civilServant.getLineManagerName(),
                civilServant.getLineManagerEmail(),
                orgs);
    }
}
