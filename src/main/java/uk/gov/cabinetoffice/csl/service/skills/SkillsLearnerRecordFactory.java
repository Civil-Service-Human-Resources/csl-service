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
import uk.gov.cabinetoffice.csl.domain.skills.UserLearnerRecordCollection;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
            log.warn("Record {} / {} was returned as a completed record but does not have a completion event. Skipping", learnerRecord.getLearnerId(), learnerRecord.getResourceId());
            return null;
        }
    }

    public List<SkillsLearnerRecord> learnerRecordsToSkillsLearnerRecord(UserLearnerRecordCollection learner) {
        return learner.getLearnerRecords().stream()
                .map(lr -> this.learnerRecordToSkillsLearnerRecord(learner.getEmail(), lr))
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
        Map<String, Collection<LearnerRecord>> learnerRecordMap = courseRecords.stream().collect(Collectors.toMap(LearnerRecord::getLearnerId, lr -> new ArrayList<>(List.of(lr)), (collection, collection2) -> {
            collection.addAll(collection2);
            return collection;
        }));
        List<UserLearnerRecordCollection> learners = metadata.getUids().stream().map(uid -> {
                    String email = uidsToEmails.get(uid);
                    Collection<LearnerRecord> learnerRecords = learnerRecordMap.getOrDefault(uid, List.of());
                    return new UserLearnerRecordCollection(uid, email, learnerRecords);
                }).sorted(Comparator.comparingInt((UserLearnerRecordCollection c) -> c.getLearnerRecords().size()).reversed())
                .toList();
        for (UserLearnerRecordCollection user : learners) {
            List<String> msgParts = new ArrayList<>(List.of(String.format("Processed user %s", user.getUid())));
            if (user.getEmail() != null) {
                msgParts.add(String.format("email is %s, %s records", user.getEmail(), user.getLearnerRecords().size()));
                if (response.getRecordCount() + user.getLearnerRecords().size() > totalRecordsInResponse) {
                    msgParts.add(String.format("skipped as there were %s record slots left", totalRecordsInResponse - response.getRecordCount()));
                } else {
                    Collection<SkillsLearnerRecord> records = learnerRecordsToSkillsLearnerRecord(user);
                    response.addUserRecords(records);
                }
            }
            log.info(String.join(", ", msgParts));
            response.addProcessedUid(user.getUid());
        }
        log.info("Processed {} users and {} learner records. There are {} users remaining", response.getUids().size(), response.getRecordCount(), response.getRemainingUsers());
        return response;
    }

}
