package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.User;

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

    public static RegistrationInput from(User user, String moduleId, String moduleRecordUid, String courseId) {
        String learnerFirstName = user.getName();
        return new RegistrationInput(
                moduleRecordUid,
                courseId,
                moduleId,
                user.getId(),
                learnerFirstName,
                ""
        );
    }
}
