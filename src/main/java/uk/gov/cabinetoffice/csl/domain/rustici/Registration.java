package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration {
    private String courseId;
    private Learner learner;
    private String registrationId;
}
