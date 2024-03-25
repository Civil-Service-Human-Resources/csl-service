package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationInput {
    private String registrationId;
    private String courseId;
    private String moduleId;
    private String learnerId;
    private String learnerFirstName;
    private String learnerLastName;

    public static RegistrationInput from(String learnerId, String moduleId, String moduleRecordUid, String courseId,
                                         UserDetailsDto userDetailsDto) {
        String learnerFirstName = userDetailsDto.getLearnerName();
        String learnerLastName = userDetailsDto.getLearnerLastName();
        return new RegistrationInput(
                moduleRecordUid,
                courseId,
                moduleId,
                learnerId,
                learnerFirstName,
                learnerLastName == null ? "" : learnerLastName
        );
    }
}
