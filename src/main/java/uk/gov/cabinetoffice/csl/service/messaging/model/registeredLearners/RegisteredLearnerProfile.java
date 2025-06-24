package uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RegisteredLearnerProfile {
    private final String uid;
    private final String email;
    private final String full_name;
    private final Integer organisation_id;
    private final String organisation_name;
    private final Integer grade_id;
    private final String grade_name;
    private final Integer profession_id;
    private final String profession_name;
}
