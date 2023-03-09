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
}
