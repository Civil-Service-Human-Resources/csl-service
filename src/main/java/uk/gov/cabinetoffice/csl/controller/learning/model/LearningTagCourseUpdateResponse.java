package uk.gov.cabinetoffice.csl.controller.learning.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LearningTagCourseUpdateResponse {
    @JsonProperty("successful_ids")
    private Collection<String> successfulIds;
    @JsonProperty("failed_ids")
    private Collection<String> failedIds;
}
