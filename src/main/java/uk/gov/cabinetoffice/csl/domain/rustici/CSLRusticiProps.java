package uk.gov.cabinetoffice.csl.domain.rustici;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.Result;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class CSLRusticiProps {
    private final String courseId;
    private final String moduleId;
    private final String learnerId;
    @Nullable
    private final Result result;
    @Nullable
    private final LocalDateTime completionDate;

    public boolean shouldProcess() {
        return result != null || completionDate != null;
    }
}
