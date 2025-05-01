package uk.gov.cabinetoffice.csl.service;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.LearningResourceType;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordQuery;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecordType;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public abstract class TypedLearnerRecordParameterFactory extends LearnerRecordParameterFactory {

    private final Map<LearningResourceType, LearnerRecordType> map;

    protected TypedLearnerRecordParameterFactory(Map<LearningResourceType, LearnerRecordType> map) {
        this.map = map;
    }

    abstract LearningResourceType getType();

    public LearnerRecordQuery getLearnerRecordQuery(Set<String> learnerIds, Set<String> resourceIds) {
        return LearnerRecordQuery.builder()
                .learnerRecordTypes(List.of(map.get(this.getType()).getId()))
                .learnerIds(learnerIds)
                .resourceIds(resourceIds)
                .build();
    }

}
