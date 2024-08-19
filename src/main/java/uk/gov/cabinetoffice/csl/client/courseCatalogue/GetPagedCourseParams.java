package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@RequiredArgsConstructor
@Builder
public class GetPagedCourseParams {

    private final boolean mandatory;
    private final Collection<String> department;

    public String getUrlParams() {
        ArrayList<String> params = new ArrayList<>();
        if (mandatory) {
            params.add("mandatory=true");
        }
        if (department != null && !department.isEmpty()) {
            params.add(String.format("department=%s", String.join(",", department)));
        }
        if (params.isEmpty()) {
            return "";
        } else {
            return String.format("?%s", String.join("&", params));
        }
    }

}
