package uk.gov.cabinetoffice.csl.domain.skills;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;

import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
public class UserLearnerRecordCollection {

    private String uid;
    @Nullable
    private String email;
    private Collection<LearnerRecord> learnerRecords;

}
