package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;

import java.util.List;
import java.util.Set;

@Service
public abstract class TypedLearnerRecordParameterFactory extends LearnerRecordParameterFactory {

    abstract LearningResourceType getType();

    public LearnerRecordQuery getLearnerRecordQuery(Set<String> learnerIds, Set<String> resourceIds) {
        return LearnerRecordQuery.builder()
                .learnerRecordTypes(List.of(this.getType().name()))
                .learnerIds(learnerIds)
                .resourceIds(resourceIds)
                .build();
    }

}
