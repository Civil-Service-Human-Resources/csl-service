package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;

import java.util.List;

@Data
@AllArgsConstructor
public class CSLRusticiProps {
    private String courseId;
    private String moduleId;
    private String learnerId;
    private List<PatchOp> moduleRecordPatches;
}
