package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.PatchOp;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.domain.rustici.RusticiRollupData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class RusticiCSLDataService {

    private List<PatchOp> getModuleUpdatesFromRollupData(RusticiRollupData rollupData) {
        LocalDateTime completedDate = rollupData.getCompletedDate();
        String result = rollupData.getRegistrationSuccess();
        ArrayList<PatchOp> patches = new ArrayList<>();
        if (completedDate != null) {
            patches.add(PatchOp.replacePatch("/state", State.COMPLETED.name()));
            patches.add(PatchOp.replacePatch("/completionDate", completedDate.toString()));
        }
        if (isNotBlank(result) && Arrays.stream(Result.values()).anyMatch(v -> v.name().equals(result))) {
            patches.add(PatchOp.replacePatch("/result", result));
        }
        return patches;
    }

    public CSLRusticiProps getCSLDataFromRollUpData(RusticiRollupData rollupData) {
        String rusticiCourseIdRegex = "\\.";
        String[] courseIdDotModuleIdParts = rollupData.getCourse().getId().split(rusticiCourseIdRegex);
        String courseId = courseIdDotModuleIdParts[0];
        String moduleId = courseIdDotModuleIdParts[1];
        String learnerId = rollupData.getLearner().getId();
        List<PatchOp> patches = getModuleUpdatesFromRollupData(rollupData);
        return new CSLRusticiProps(
                courseId,
                moduleId,
                learnerId,
                patches
        );
    }
}
