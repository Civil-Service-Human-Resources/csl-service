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
public class LearningTagCourseUpdateResponse extends LearningTagUpdateResponse {
    private Long learningTagId;

    public LearningTagCourseUpdateResponse(Collection<String> successfulIds, Collection<String> failedIds, Long learningTagId) {
        super(successfulIds, failedIds);
        this.learningTagId = learningTagId;
    }
}
