package uk.gov.cabinetoffice.csl.client.csrs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@ConfigurationProperties(prefix = "csrs")
@RequiredArgsConstructor
@Valid
@Validated
public class CsrsConfiguration {
    @NotNull
    private final Integer organisationalUnitMaxPageSize;
    @NotNull
    private final String civilServants;
    @NotNull
    private final String allOrganisationalUnits;
    @NotNull
    private final String organisationalUnits;
    @NotNull
    private final String professionsTree;
    @NotNull
    private final String agencyToken;
    @NotNull
    private final String domains;
    @NotNull
    private final String grades;

    public String getCivilServantProfileUrl(String uid) {
        return String.format("%s/resource/%s/profile", civilServants, uid);
    }

    public String getCivilServantMeUrl() {
        return String.format("%s/me", civilServants);
    }

    public String getCivilServantMeOrganisationUrl() {
        return String.format("%s/me/organisationalUnit", civilServants);
    }

    public String getOrganisationalUnitUrl(Long organisationalUnitId) {
        return String.format("%s/%s", organisationalUnits, organisationalUnitId);
    }
    
    public String getAgencyTokenUrl(Long organisationalUnitId) {
        return String.format("%s/%s", getOrganisationalUnitUrl(organisationalUnitId), agencyToken);
    }

    public String getDomainsUrl(Long organisationalUnitId) {
        return String.format("%s/domains", getOrganisationalUnitUrl(organisationalUnitId));
    }

    public String getDomainsUrl(Long organisationalUnitId, Long domainId) {
        return String.format("%s/domains/%s", getOrganisationalUnitUrl(organisationalUnitId), domainId);
    }
}
