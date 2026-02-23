package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
public class CivilServantSkillsMetadataCollection {

    private List<String> uids;
    private LocalDateTime minLastSyncDate;
    private Integer totalUids;

}
