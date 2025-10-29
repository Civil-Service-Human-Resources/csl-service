package uk.gov.cabinetoffice.csl.controller.csrs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationalUnitDto {
    private String code;
    private String name;
    private String abbreviation;
    private Long parentId;

    @JsonIgnore
    public Long getParentIdSafe() {
        return parentId == null ? 0L : parentId;
    }
}
