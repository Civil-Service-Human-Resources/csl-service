package uk.gov.cabinetoffice.csl.service.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServantSkillsMetadataCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.course.CourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordCollection;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordEvent;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecord;
import uk.gov.cabinetoffice.csl.domain.skills.SkillsLearnerRecordResponse;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class SkillsLearnerRecordFactory {

    private final Integer totalRecordsInResponse;

    public SkillsLearnerRecordFactory(@Value("${skills.maxLearnerRecords}") Integer totalRecordsInResponse) {
        this.totalRecordsInResponse = totalRecordsInResponse;
    }

    public SkillsLearnerRecord learnerRecordToSkillsLearnerRecord(String emailAddress, LearnerRecord learnerRecord) {
        LocalDate completionDate = null;
        LearnerRecordEvent latestEvent = learnerRecord.getLatestEvent();
        if (latestEvent != null && latestEvent.getActionType().equals(CourseRecordAction.COMPLETE_COURSE)) {
            completionDate = learnerRecord.getLatestEvent().getEventTimestamp().toLocalDate();
        }
        if (completionDate != null) {
            return new SkillsLearnerRecord(emailAddress, learnerRecord.getResourceId(), 100,
                    true, "", 0, learnerRecord.getCreatedTimestamp().toLocalDate(),
                    completionDate);
        } else {
            log.info("Record {} / {} was returned as a completed record but does not have a completion event. Skipping", learnerRecord.getLearnerId(), learnerRecord.getResourceId());
            return null;
        }
    }

    public List<SkillsLearnerRecord> learnerRecordsToSkillsLearnerRecord(String emailAddress, Collection<LearnerRecord> learnerRecords) {
        return learnerRecords.stream()
                .map(lr -> this.learnerRecordToSkillsLearnerRecord(emailAddress, lr))
                .filter(Objects::nonNull).toList();
    }

    public SkillsLearnerRecordResponse buildResponse(CivilServantSkillsMetadataCollection metadata,
                                                     Map<String, String> uidsToEmails, LearnerRecordCollection courseRecords) {
        SkillsLearnerRecordResponse response = new SkillsLearnerRecordResponse(new ArrayList<>(), 0, metadata.getTotalUids(), new ArrayList<>());
        if (uidsToEmails.isEmpty() || courseRecords.isEmpty()) {
            log.info("0 emails found or 0 learner records found for UIDs, returning");
            response.addUnprocessedUsers(metadata.getUids());
            return response;
        }
        Map<String, Collection<LearnerRecord>> learnerRecordMap = courseRecords.getOrderedMapByUser(LearnerRecordCollection.COMPARATOR_NUMBER_OF_RECORDS_DESC);
        // Order learner records by user record count descending
        // Process users in order, starting with users that have the largest records.
        // Break when there isn't enough space in the response for anymore records.
        for (String learnerId : learnerRecordMap.keySet()) {
            Collection<LearnerRecord> learnerRecords = learnerRecordMap.get(learnerId);
            if (response.getRecordCount() + learnerRecords.size() > totalRecordsInResponse) {
                log.info("{} record slots left whereas learner record size is {}, skipping", totalRecordsInResponse - response.getRecordCount(), learnerRecords.size());
                continue;
            }
            log.debug("Processing user {} ({} records)", learnerId, learnerRecords.size());
            String email = uidsToEmails.get(learnerId);
            Collection<SkillsLearnerRecord> records = new ArrayList<>();
            if (email != null) {
                records = learnerRecordsToSkillsLearnerRecord(email, learnerRecords);
            }
            response.addUserRecords(learnerId, records);
            uidsToEmails.remove(learnerId);
            if (Objects.equals(response.getRecordCount(), totalRecordsInResponse)) {
                break;
            }
        }
        // Add the UIDs that did not have any learner records associated with them
        response.addUnprocessedUsers(uidsToEmails.keySet().stream().filter(uid -> !learnerRecordMap.containsKey(uid)).toList());
        log.info("Processed {} users and {} learner records. There are {} users remaining", response.getUids().size(), response.getRecordCount(), response.getRemainingUsers());
        return response;
    }

}
