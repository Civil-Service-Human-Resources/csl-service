package uk.gov.cabinetoffice.csl.domain.skills;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillsLearnerRecordPagedResponse extends PagedResponse<SkillsLearnerRecord> {

    public SkillsLearnerRecordPagedResponse(List<SkillsLearnerRecord> content, boolean last, Integer number, Integer totalPages, Integer totalElements, Integer size) {
        super(content, last, number, totalPages, totalElements, size);
    }
}
