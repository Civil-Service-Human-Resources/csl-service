package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetOptionalLearningRecordParams {
    int page = 0;
    int size = 20;
    String q;
    Sort.Direction sort = Sort.Direction.ASC;
}
