package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated We should move away from using course records as it is a legacy data item.
 * <p>
 * Use The ILearnerRecord.java interface for calculating course state
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRecords {
    public List<CourseRecord> courseRecords = new ArrayList<>();

    public CourseRecords(CourseRecord courseRecord) {
        this(List.of(courseRecord));
    }
}
