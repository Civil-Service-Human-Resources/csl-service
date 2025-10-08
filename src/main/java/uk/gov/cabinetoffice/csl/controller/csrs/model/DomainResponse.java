package uk.gov.cabinetoffice.csl.controller.csrs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DomainResponse {
    private List<Long> updatedIds;
}
