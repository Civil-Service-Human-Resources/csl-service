package uk.gov.cabinetoffice.csl.client.courseCatalogue;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@RequiredArgsConstructor
@Builder
public class GetCourseParams {

    private final Collection<String> courseIds;
    private final boolean mandatory;
    private final Collection<String> department;

    public String getUrlParams() {
        ArrayList<String> params = new ArrayList<>();
        if (courseIds != null && !courseIds.isEmpty()) {
            params.add(String.format("courseId=%s", String.join(",", courseIds)));
        }
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
