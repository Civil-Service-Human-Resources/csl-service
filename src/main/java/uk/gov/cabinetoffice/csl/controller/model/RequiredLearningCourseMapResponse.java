package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minidev.json.annotate.JsonIgnore;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.learning.requiredLearning.CourseWithLearningPeriods;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Audience;
import uk.gov.cabinetoffice.csl.util.TuplePair;

import java.util.*;

@AllArgsConstructor
@Getter
public class RequiredLearningCourseMapResponse extends HashMap<String, CourseWithLearningPeriods> {

    private final Map<String, Collection<TuplePair<String, String>>> orgCodeToCourseAudienceIdMap;

    @JsonIgnore
    private boolean addOrganisationToLearningPeriodIfPresent(String courseId, String audienceId, Integer organisationId) {
        return Optional.ofNullable(get(courseId))
                .map(map -> map.getLearningPeriods().get(audienceId))
                .map(lp -> {
                    lp.getOrganisationIds().add(organisationId);
                    return true;
                }).orElse(false);
    }

    @JsonIgnore
    public void checkAndStoreOrganisationHierarchy(List<OrganisationalUnit> orgHierarchy) {
        Set<String> courseIdsForOrg = new HashSet<>();
        orgHierarchy.forEach(organisation ->
                Optional.ofNullable(orgCodeToCourseAudienceIdMap.get(organisation.getCode()))
                        .ifPresent(courseAudienceIds -> courseAudienceIds.forEach(courseAudienceId -> {
                            String courseId = courseAudienceId.a();
                            if (!courseIdsForOrg.contains(courseId)) {
                                if (addOrganisationToLearningPeriodIfPresent(courseId, courseAudienceId.b(), orgHierarchy.get(0).getId().intValue())) {
                                    courseIdsForOrg.add(courseId);
                                }
                            }
                        }))
        );
    }

    @JsonIgnore
    public void addAudiences(String courseId, String courseTitle, Collection<Audience> audiences) {
        CourseWithLearningPeriods courseWithLearningPeriods = new CourseWithLearningPeriods(courseTitle, new HashMap<>());
        audiences.forEach(a -> Optional.ofNullable(a.getLearningPeriod())
                .ifPresent(lp -> {
                    String audId = a.getId();
                    courseWithLearningPeriods.addAudience(a);
                    a.getDepartments().forEach(orgCode -> orgCodeToCourseAudienceIdMap.computeIfAbsent(orgCode, k -> new ArrayList<>())
                            .add(new TuplePair<>(courseId, audId)));
                }));
        put(courseId, courseWithLearningPeriods);
    }

}
