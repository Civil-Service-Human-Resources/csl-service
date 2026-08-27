package uk.gov.cabinetoffice.csl.controller.learning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LearningTagHyperlinkUpdateRequest {
    private List<String> ids;
}
