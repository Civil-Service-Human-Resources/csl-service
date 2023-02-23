package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaunchLinkRequest {
    private int expiry;
    private String redirectOnExitUrl;
    private Collection<AdditionalValue> additionalValues;
}
