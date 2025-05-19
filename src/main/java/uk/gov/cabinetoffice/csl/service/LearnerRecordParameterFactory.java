package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ILearnerRecordResourceID;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LearnerRecordParameterFactory {

    public LearnerRecordQuery getLearnerRecordQuery(Set<String> learnerIds, Set<String> resourceIds) {
        return LearnerRecordQuery.builder()
                .learnerIds(learnerIds)
                .resourceIds(resourceIds)
                .build();
    }

    public LearnerRecordQuery getLearnerRecordQuery(String learnerId) {
        return getLearnerRecordQuery(Set.of(learnerId), null);
    }

    public LearnerRecordQuery getLearnerRecordQuery(List<? extends ILearnerRecordResourceID> ids) {
        Set<String> resourceIds = new HashSet<>();
        Set<String> learnerIds = new HashSet<>();
        ids.forEach(id -> {
            resourceIds.add(id.getResourceId());
            learnerIds.add(id.getLearnerId());
        });
        return getLearnerRecordQuery(learnerIds, resourceIds);
    }
}
