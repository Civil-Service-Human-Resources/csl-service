package uk.gov.cabinetoffice.csl.service.csrs;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.client.identity.UidRequest;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.csrs.record.CivilServantSkillsMetadataPagedResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CivilServantRegistryService {

    private final ICSRSClient civilServantRegistryClient;

    public List<AreaOfWork> getAreasOfWork() {
        return civilServantRegistryClient.getAreasOfWork();
    }

    public List<Grade> getGrades() {
        return civilServantRegistryClient.getGrades();
    }

    public void patchCivilServant(PatchCivilServantDto patch) {
        civilServantRegistryClient.patchCivilServant(patch);
    }

    public void patchCivilServantOrganisation(OrganisationalUnitIdDto organisationalUnitIdDTO) {
        civilServantRegistryClient.patchCivilServantOrganisation(organisationalUnitIdDTO);
    }

    public CivilServantSkillsMetadataCollection getCivilServantSkillsMetadata(Integer size, boolean isSynced) {
        CivilServantSkillsMetadataPagedResponse resp = civilServantRegistryClient.getSkillsCivilServants(size, isSynced);
        Integer totalUids = resp.getTotalElements();
        List<String> uids = new ArrayList<>();
        LocalDateTime minLastSyncDate = null;
        for (CivilServantSkillsMetadata civilServantSkillsMetadata : resp.getContent()) {
            String uid = civilServantSkillsMetadata.getUid();
            if (minLastSyncDate == null || civilServantSkillsMetadata.getSyncTimestamp().isBefore(minLastSyncDate)) {
                minLastSyncDate = civilServantSkillsMetadata.getSyncTimestamp();
            }
            uids.add(uid);
        }
        return new CivilServantSkillsMetadataCollection(uids, minLastSyncDate, totalUids);
    }

    public void syncSkillsMetadata(Collection<String> uids) {
        UidRequest request = new UidRequest(uids);
        civilServantRegistryClient.syncSkillsMetadata(request);
    }
}
