package uk.gov.cabinetoffice.csl.domain.skills;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillsLearnerRecordResponse {

    private List<SkillsLearnerRecord> results;
    private Integer recordCount;
    private Integer userCount;
    private Integer remainingUsers;

    @JsonIgnore
    private Collection<String> uids;

}
