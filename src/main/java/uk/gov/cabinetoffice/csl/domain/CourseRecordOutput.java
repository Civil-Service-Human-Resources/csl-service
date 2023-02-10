package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseRecordOutput {
    public List<CourseRecord> courseRecords;
}
