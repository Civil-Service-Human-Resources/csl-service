package uk.gov.cabinetoffice.csl.controller.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience.Type;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdminAudienceOverview {

    private String audienceName;
    private Type audienceType;
    private Collection<String> organisations;
    private Collection<String> grades;
    private Collection<String> areasOfWork;
    private Collection<String> interests;
    private String requiredBy;
}
