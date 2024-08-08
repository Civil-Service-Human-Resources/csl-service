package uk.gov.cabinetoffice.csl.domain.identity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IdentityDto {

    private String uid;
    private List<String> roles;

    public boolean hasRole(String role) {
        return this.roles.contains(role);
    }
}
