package uk.gov.cabinetoffice.csl.domain.learning;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Learning {

    private final List<DisplayCourse> courses;

}
