package uk.gov.cabinetoffice.csl.domain.learnerrecord.invite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class InviteDto {

    private String learnerEmail;
    private String learnerUid;

}
