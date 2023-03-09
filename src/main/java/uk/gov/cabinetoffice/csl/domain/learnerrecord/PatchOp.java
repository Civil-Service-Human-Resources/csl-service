package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchOp {
    private String op;
    private String path;
    private String value;
}
