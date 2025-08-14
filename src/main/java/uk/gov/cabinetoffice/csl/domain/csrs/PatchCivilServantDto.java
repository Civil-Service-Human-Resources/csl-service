package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.Builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
public class PatchCivilServantDto {

    private final List<AreaOfWork> otherAreasOfWork;
    private final String fullName;
    private final Grade grade;
    private final AreaOfWork profession;
    private final OrganisationalUnit organisationalUnit;

    public Map<String, Object> getAsApiParams() {
        Map<String, Object> apiParams = new HashMap<>();
        if (otherAreasOfWork != null) {
            apiParams.put("otherAreasOfWork", otherAreasOfWork.stream().map(p -> String.format("/professions/%s", p.getId())));
        }
        if (fullName != null) {
            apiParams.put("fullName", fullName);
        }
        if (grade != null) {
            apiParams.put("grade", String.format("/grades/%s", grade.getId()));
        }
        if (profession != null) {
            apiParams.put("profession", String.format("/profession/%s", profession.getId()));
        }
        if (organisationalUnit != null) {
            apiParams.put("organisationalUnit", String.format("/organisationalUnit/%s", organisationalUnit.getId()));
        }
        return apiParams;
    }
}
