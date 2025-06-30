package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@Builder
@RequiredArgsConstructor
public class GetModuleRecordParams {

    private final Set<String> userIds;
    private final Set<String> moduleIds;

}
