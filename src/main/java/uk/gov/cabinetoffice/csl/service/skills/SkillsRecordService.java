package uk.gov.cabinetoffice.csl.service.skills;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.identity.IIdentityAPIClient;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.GetSkillsLearnerRecordsParams;
import uk.gov.cabinetoffice.csl.controller.learnerRecord.model.SkillsSyncMode;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServantSkillsMetadataCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordCollection;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordResponse;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

import java.util.Map;

@Service
public class SkillsRecordService {

    private final IIdentityAPIClient identityAPIClient;
    private final CivilServantRegistryService civilServantRegistryService;
    private final LearnerRecordService learnerRecordService;
    private final SkillsLearnerRecordFactory skillsLearnerRecordFactory;

    public SkillsRecordService(IIdentityAPIClient identityAPIClient, CivilServantRegistryService civilServantRegistryService, LearnerRecordService learnerRecordService, SkillsLearnerRecordFactory skillsLearnerRecordFactory) {
        this.identityAPIClient = identityAPIClient;
        this.civilServantRegistryService = civilServantRegistryService;
        this.learnerRecordService = learnerRecordService;
        this.skillsLearnerRecordFactory = skillsLearnerRecordFactory;
    }

    public SkillsLearnerRecordResponse getSkillsLearnerRecords(GetSkillsLearnerRecordsParams params) {
        CivilServantSkillsMetadataCollection metadata = civilServantRegistryService.getCivilServantSkillsMetadata(params.getSize(), params.getMode() == SkillsSyncMode.DELTA);
        Map<String, String> uidsToEmails = identityAPIClient.getUidToEmailMap(metadata.getUids());
        LearnerRecordCollection learnerRecords = learnerRecordService.searchCompletedLearnerRecords(uidsToEmails.keySet(), metadata.getMinLastSyncDate());
        SkillsLearnerRecordResponse resp = skillsLearnerRecordFactory.buildResponse(uidsToEmails, learnerRecords, metadata.getTotalUids());
        civilServantRegistryService.syncSkillsMetadata(resp.getUids());
        return resp;
    }

}
