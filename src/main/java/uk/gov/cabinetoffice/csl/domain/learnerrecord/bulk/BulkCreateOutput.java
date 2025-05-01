package uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkCreateOutput<Output, Input> {

    private List<Output> successfulResources;
    private List<FailedResource<Input>> failedResources;

}
