package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.csrs.BasicOrganisationalUnit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private final String id;
    private String email;
    private String name;
    private Long organisationId;
    private Integer professionId;
    private String professionName;
    private Integer gradeId;
    private String gradeName;
    private String lineManagerName;
    private String lineManagerEmail;
    private ArrayList<BasicOrganisationalUnit> departmentHierarchy = new ArrayList<>();

    public String getFormattedOrganisationName() {
        return IntStream.range(0, departmentHierarchy.size())
                .mapToObj(i -> departmentHierarchy.get(departmentHierarchy.size() - 1 - i))
                .map(BasicOrganisationalUnit::getName).collect(Collectors.joining(" | "));
    }

    public List<String> getDepartmentCodes() {
        return departmentHierarchy.stream().map(BasicOrganisationalUnit::getCode).collect(Collectors.toList());
    }

    public boolean hasLineManager() {
        return isNotBlank(lineManagerEmail) && isNotBlank(lineManagerName);
    }

}
