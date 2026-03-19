package uk.gov.cabinetoffice.csl.service.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.GetSkillsLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.SearchLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.SkillsSyncMode;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServantSkillsMetadataCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordPagedResponse;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordPagedResponse;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordResponse;
import uk.gov.cabinetoffice.csl.service.IdentityAPIService;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

@Service
@Slf4j
public class SkillsRecordService {

    private final Clock clock;
    private final Period defaultPeriod;
    private final IdentityAPIService identityAPIService;
    private final CivilServantRegistryService civilServantRegistryService;
    private final LearnerRecordService learnerRecordService;
    private final OrganisationalUnitService organisationalUnitService;
    private final SkillsLearnerRecordFactory skillsLearnerRecordFactory;

    public SkillsRecordService(Clock clock, @Value("${skills.defaultDeltaPeriod}") Period defaultPeriod, IdentityAPIService identityAPIService,
                               CivilServantRegistryService civilServantRegistryService,
                               LearnerRecordService learnerRecordService, OrganisationalUnitService organisationalUnitService, SkillsLearnerRecordFactory skillsLearnerRecordFactory) {
        this.clock = clock;
        this.defaultPeriod = defaultPeriod;
        this.identityAPIService = identityAPIService;
        this.civilServantRegistryService = civilServantRegistryService;
        this.learnerRecordService = learnerRecordService;
        this.organisationalUnitService = organisationalUnitService;
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
            uidsToEmails = identityAPIService.getUidsToEmailsMapWithUids(metadata.getUids());
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

    public SkillsLearnerRecordPagedResponse searchForLearnerRecords(String organisationCode, SearchLearnerRecordsParams params,
                                                                    Pageable pageableParams) {
        Set<String> allowlistedDomains = organisationalUnitService.getAllowlistedDomainsWithOrgCode(organisationCode, true);
        List<String> emails = params.getEmails().stream()
                .filter(e -> allowlistedDomains.contains(e.split("@")[1])).toList();
        Map<String, String> uidsToEmails = identityAPIService.getUidsToEmailsMapWithEmails(emails);
        LearnerRecordPagedResponse resp = learnerRecordService.searchCompletedLearnerRecords(new HashSet<>(uidsToEmails.keySet()), params.getCompletedDateGte(), pageableParams);
        return skillsLearnerRecordFactory.buildResponse(resp, uidsToEmails);
    }
}
