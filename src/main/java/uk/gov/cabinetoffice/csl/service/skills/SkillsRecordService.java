package uk.gov.cabinetoffice.csl.service.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.identity.IIdentityAPIClient;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.GetSkillsLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.SkillsSyncMode;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServantSkillsMetadataCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordCollection;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordResponse;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SkillsRecordService {

    private final Clock clock;
    private final Period defaultPeriod;
    private final IIdentityAPIClient identityAPIClient;
    private final CivilServantRegistryService civilServantRegistryService;
    private final LearnerRecordService learnerRecordService;
    private final SkillsLearnerRecordFactory skillsLearnerRecordFactory;

    public SkillsRecordService(Clock clock, @Value("${skills.defaultDeltaPeriod}") Period defaultPeriod, IIdentityAPIClient identityAPIClient,
                               CivilServantRegistryService civilServantRegistryService,
                               LearnerRecordService learnerRecordService, SkillsLearnerRecordFactory skillsLearnerRecordFactory) {
        this.clock = clock;
        this.defaultPeriod = defaultPeriod;
        this.identityAPIClient = identityAPIClient;
        this.civilServantRegistryService = civilServantRegistryService;
        this.learnerRecordService = learnerRecordService;
        this.skillsLearnerRecordFactory = skillsLearnerRecordFactory;
    }

    public SkillsLearnerRecordResponse getSkillsLearnerRecords(GetSkillsLearnerRecordsParams params) {
        LocalDateTime lastSyncTimestampLte = null;
        if (params.getMode() == SkillsSyncMode.DELTA) {
            lastSyncTimestampLte = LocalDateTime.now(clock).minus(defaultPeriod);
            log.info("Getting all skills users where last synced timestamp <= {}", lastSyncTimestampLte);
        } else {
            log.info("Getting all skills users that have not been synced");
        }
        Map<String, String> uidsToEmails = new HashMap<>();
        LearnerRecordCollection learnerRecords = new LearnerRecordCollection();
        CivilServantSkillsMetadataCollection metadata = civilServantRegistryService.getCivilServantSkillsMetadata(params.getSize(), lastSyncTimestampLte);
        if (!metadata.getUids().isEmpty()) {
            uidsToEmails = identityAPIClient.getUidToEmailMap(metadata.getUids());
        }
        if (!uidsToEmails.isEmpty()) {
            learnerRecords = learnerRecordService.searchCompletedLearnerRecords(uidsToEmails.keySet(), metadata.getMinLastSyncDate());
        }
        SkillsLearnerRecordResponse response = skillsLearnerRecordFactory.buildResponse(metadata, uidsToEmails, learnerRecords);
        if (!response.getUids().isEmpty()) {
            civilServantRegistryService.syncSkillsMetadata(response.getUids());
        }
        return response;
    }

}
