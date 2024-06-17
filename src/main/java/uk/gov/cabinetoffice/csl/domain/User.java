package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.rustici.UserDetailsDto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private final String id;
    private String email;
    private Integer organisationId;
    private String organisationAbbreviation;
    private Integer professionId;
    private String professionName;
    private Integer gradeId;
    private String gradeCode;
    private Collection<String> departmentCodes = List.of();

    public static User fromUserDetails(String uid, UserDetailsDto userDetailsDto) {
        return new User(uid, userDetailsDto.getLearnerEmail(), userDetailsDto.getOrganisationId(), userDetailsDto.getOrganisationAbbreviation(),
                userDetailsDto.getProfessionId(), userDetailsDto.getProfessionName(), userDetailsDto.getGradeId(), userDetailsDto.getGradeCode(), userDetailsDto.getUserDepartmentHierarchy());
    }
    
}
