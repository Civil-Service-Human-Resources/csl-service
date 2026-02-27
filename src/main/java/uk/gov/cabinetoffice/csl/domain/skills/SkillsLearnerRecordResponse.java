package uk.gov.cabinetoffice.csl.domain.skills;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillsLearnerRecordResponse {

    private Collection<SkillsLearnerRecord> results = new ArrayList<>();
    private Integer userCount = 0;
    private Integer remainingUsers = 0;
    @JsonIgnore
    private Collection<String> uids;
    
    public Integer getRecordCount() {
        return results.size();
    }

    public void addUserRecords(String learnerId, Collection<SkillsLearnerRecord> userRecords) {
        results.addAll(userRecords);
        if (!userRecords.isEmpty()) {
            userCount++;
        }
        uids.add(learnerId);
        remainingUsers--;
    }

    public void addUnprocessedUsers(Collection<String> uids) {
        this.uids.addAll(uids);
        remainingUsers = remainingUsers - uids.size();
    }

}
