package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailAddressDto {

    @NotNull
    @Email
    private String learnerEmail;

}
