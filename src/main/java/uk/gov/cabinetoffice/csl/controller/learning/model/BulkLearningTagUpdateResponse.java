package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkLearningTagUpdateResponse {
    private Collection<LearningTagCourseUpdateResponse> successfulIds;
    private Collection<LearningTagCourseUpdateResponse> failedIds;
}
