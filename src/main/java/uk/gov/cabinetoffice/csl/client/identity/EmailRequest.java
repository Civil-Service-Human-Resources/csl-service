package uk.gov.cabinetoffice.csl.client.identity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class EmailRequest {

    Collection<String> emails;

}
