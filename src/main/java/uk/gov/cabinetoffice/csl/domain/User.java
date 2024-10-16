package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.csrs.BasicOrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;

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
    private Integer professionId;
    private String professionName;
    private Integer gradeId;
    private String gradeName;
    private String lineManagerName;
    private String lineManagerEmail;
    private ArrayList<BasicOrganisationalUnit> departmentHierarchy = new ArrayList<>();

    public static User fromUserDetails(String uid, UserDetailsDto userDetailsDto) {
        return new User(uid, userDetailsDto.getLearnerEmail(), userDetailsDto.getLearnerName(), userDetailsDto.getProfessionId(),
                userDetailsDto.getProfessionName(), userDetailsDto.getGradeId(), userDetailsDto.getGradeName(),
                userDetailsDto.getLineManagerName(), userDetailsDto.getLineManagerEmail(),
                userDetailsDto.getDepartmentHierarchy());
    }

    public String getFormattedOrganisationName() {
        return IntStream.range(0, departmentHierarchy.size())
                .mapToObj(i -> departmentHierarchy.get(departmentHierarchy.size() - 1 - i))
                .map(BasicOrganisationalUnit::getName).collect(Collectors.joining(" | "));
    }

    public List<String> getDepartmentCodes() {
        return departmentHierarchy.stream().map(BasicOrganisationalUnit::getCode).collect(Collectors.toList());
    }

    public Integer getOrganisationId() {
        return departmentHierarchy.stream().findFirst().map(BasicOrganisationalUnit::getId).orElse(0);
    }

    public String getOrganisationName() {
        return departmentHierarchy.stream().findFirst().map(BasicOrganisationalUnit::getName).orElse("");
    }

    public boolean hasLineManager() {
        return isNotBlank(lineManagerEmail) && isNotBlank(lineManagerName);
    }

}
