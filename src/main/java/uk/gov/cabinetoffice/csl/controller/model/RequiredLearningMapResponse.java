package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RequiredLearningMapResponse {

    private Map<Long, List<CourseWithTitle>> departmentMap;

}
