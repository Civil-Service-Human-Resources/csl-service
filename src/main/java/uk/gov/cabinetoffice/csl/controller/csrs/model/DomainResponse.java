package uk.gov.cabinetoffice.csl.controller.csrs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.cabinetoffice.csl.domain.csrs.Domain;

import java.util.List;

@Getter
@AllArgsConstructor
public class DomainResponse {
    private Domain domain;
    private List<Long> updatedIds;
}
