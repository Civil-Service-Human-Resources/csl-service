package uk.gov.cabinetoffice.csl.controller.admin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag.LearningTagOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.CourseOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.ModuleOverview;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminCourseOverview extends CourseOverview {

    public AdminCourseOverview(Course course,
                               List<LearningTagOverview> tags,
                               List<ModuleOverview> modules,
                               String nextAvailableDate,
                               Collection<AdminFaceToFaceModuleOverview> faceToFaceModuleOverviews,
                               Collection<AdminAudienceOverview> audiences) {
        super(course, tags, null, modules, 0);
        this.nextAvailableDate = nextAvailableDate;
        this.faceToFaceModuleOverviews = faceToFaceModuleOverviews;
        this.audiences = audiences;
    }

    private String nextAvailableDate;
    private Collection<AdminFaceToFaceModuleOverview> faceToFaceModuleOverviews;
    private Collection<AdminAudienceOverview> audiences;
}
