package uk.gov.cabinetoffice.csl.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.client.identity.IIdentityAPIClient;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.error.NotFoundException;
import uk.gov.cabinetoffice.csl.domain.identity.Identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsService {

    private final IIdentityAPIClient identityAPIClient;
    private final ICSRSClient csrsClient;

    @Value("${identity.identityMapMaxBatchSize}")
    private Integer identityMapMaxBatchSize;

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

    public Identity getUserWithEmail(String email) {
        return identityAPIClient.getIdentityWithEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("User with email %s was not found", email)));
    }

    public Map<String, String> fetchEmailsByUids(List<String> uids) {
        return fetchByUids(uids).entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getUsername()
                ));
    }

    public Map<String, Identity> fetchByUids(List<String> uids) {
        Map<String, Identity> identitiesMap = new HashMap<>();
        List<List<String>> batchedUids = IntStream.iterate(0, i -> i + identityMapMaxBatchSize)
                .limit((int) Math.ceil((double) uids.size() / identityMapMaxBatchSize))
                .mapToObj(i -> uids.subList(i, Math.min(i + identityMapMaxBatchSize, uids.size())))
                .toList();

        batchedUids.forEach(batch -> {
            Map<String, Identity> identitiesFromUids = identityAPIClient.fetchByUids(batch);
            if (identitiesFromUids != null) {
                identitiesMap.putAll(identitiesFromUids);
            }
        });

        return identitiesMap;
    }
}
