package uk.gov.cabinetoffice.csl.service.skills;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
        boolean isCompleted = completionDate != null;
        Integer progress = isCompleted ? 100 : 0;
        return new SkillsLearnerRecord(emailAddress, learnerRecord.getResourceId(), progress,
                isCompleted, "", 0, learnerRecord.getCreatedTimestamp().toLocalDate(),
                completionDate);
    }

    public List<SkillsLearnerRecord> learnerRecordsToSkillsLearnerRecord(String emailAddress, Collection<LearnerRecord> learnerRecords) {
        return learnerRecords.stream().map(lr -> this.learnerRecordToSkillsLearnerRecord(emailAddress, lr)).toList();
    }

    public SkillsLearnerRecordResponse buildResponse(Map<String, String> uidsToEmails, LearnerRecordCollection courseRecords,
                                                     Integer totalUsers) {
        List<SkillsLearnerRecord> skillsLearnerRecords = new ArrayList<>();
        Map<String, Collection<LearnerRecord>> learnerRecordMap = courseRecords.getOrderedMapByUser(LearnerRecordCollection.COMPARATOR_NUMBER_OF_RECORDS_DESC);
        // Order learner records by user record count descending
        int recordsProcessed = 0;
        Set<String> learnerIdsProcessed = new HashSet<>();
        // Process users in order, starting with users that have the largest records.
        // Break when there isn't enough space in the response for anymore records. These records can be processed in the next call
        Integer processedCount = 0;
        for (String learnerId : learnerRecordMap.keySet()) {
            String email = uidsToEmails.get(learnerId);
            if (email != null) {
                Collection<LearnerRecord> learnerRecords = learnerRecordMap.get(learnerId);
                if (recordsProcessed + learnerRecords.size() > totalRecordsInResponse) {
                    log.info("{} record slots left whereas learner record size is {}, skipping", totalRecordsInResponse - recordsProcessed, learnerRecords.size());
                    continue;
                }
                log.debug("Processing user {} ({} records)", learnerId, learnerRecords.size());
                skillsLearnerRecords.addAll(learnerRecordsToSkillsLearnerRecord(email, learnerRecords));
                recordsProcessed += learnerRecords.size();
                learnerIdsProcessed.add(learnerId);
                processedCount++;
            }
            uidsToEmails.remove(learnerId);
            if (skillsLearnerRecords.size() == totalRecordsInResponse) {
                break;
            }
        }

        learnerIdsProcessed.addAll(uidsToEmails.keySet().stream().filter(uid -> !learnerRecordMap.containsKey(uid)).toList());
        Integer remainingUserCount = totalUsers - learnerIdsProcessed.size();
        log.info("Processed {} users and {} learner records. There are {} users remaining", learnerIdsProcessed.size(), skillsLearnerRecords.size(), remainingUserCount);
        return new SkillsLearnerRecordResponse(skillsLearnerRecords, skillsLearnerRecords.size(),
                processedCount, remainingUserCount, learnerIdsProcessed);
    }

}
