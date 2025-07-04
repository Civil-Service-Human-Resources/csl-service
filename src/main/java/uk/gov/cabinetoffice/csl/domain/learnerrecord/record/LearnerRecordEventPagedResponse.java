package uk.gov.cabinetoffice.csl.domain.learnerrecord.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LearnerRecordEventPagedResponse extends PagedResponse<LearnerRecordEvent> {
}
