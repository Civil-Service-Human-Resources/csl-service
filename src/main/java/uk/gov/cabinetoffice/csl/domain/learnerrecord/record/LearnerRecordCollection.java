package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import java.util.*;
import java.util.stream.Collectors;

public class LearnerRecordCollection extends ArrayList<LearnerRecord> {

    public static final Comparator<Collection<LearnerRecord>> COMPARATOR_NUMBER_OF_RECORDS_DESC = Comparator.comparingInt((Collection<LearnerRecord> c) -> c.size()).reversed();

    public LearnerRecordCollection() {
        super();
    }

    public LearnerRecordCollection(Collection<? extends LearnerRecord> c) {
        super(c);
    }

    public Map<String, Collection<LearnerRecord>> getOrderedMapByUser(Comparator<Collection<LearnerRecord>> comparator) {
        LinkedHashMap<String, Collection<LearnerRecord>> learnerRecordMap = new LinkedHashMap<>();
        stream()
                .collect(Collectors.toMap(LearnerRecord::getLearnerId, lr -> new ArrayList<>(List.of(lr)), (collection, collection2) -> {
                    collection.addAll(collection2);
                    return collection;
                }))
                .values().stream().sorted(comparator)
                .forEach(learnerRecords -> learnerRecordMap.put(learnerRecords.get(0).getLearnerId(), learnerRecords));
        return learnerRecordMap;
    }
}
