package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class PatchCivilServantDto {

    private final List<AreaOfWork> otherAreasOfWork;

    public Map<String, Object> getAsApiParams() {
        Map<String, Object> apiParams = new HashMap<>();
        if (otherAreasOfWork != null) {
            apiParams.put("otherAreasOfWork", otherAreasOfWork.stream().map(p -> String.format("/professions/%s", p.getId())));
        }
        return apiParams;
    }

}
