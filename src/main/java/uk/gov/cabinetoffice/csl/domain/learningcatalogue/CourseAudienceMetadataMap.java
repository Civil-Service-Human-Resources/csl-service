package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.AreaOfWork;
import uk.gov.cabinetoffice.csl.domain.csrs.Interest;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseAudienceMetadataMap implements Serializable {

    private Map<String, Collection<String>> departments;
    private Map<String, Collection<String>> areasOfWork;
    private Map<String, Collection<String>> interests;

    public LinkedHashMap<String, Collection<String>> filterForUser(User user, Collection<String> learningRecordIds) {
        List<String> courseIds = new ArrayList<>(learningRecordIds);
        LinkedHashMap<String, Collection<String>> map = new LinkedHashMap<>();
        map.put(user.getOrganisationName(), new ArrayList<>());
        Map.of(
                departments, user.getDepartmentCodes(),
                areasOfWork, user.getAllAreasOfWork().stream().map(AreaOfWork::getName).toList(),
                interests, user.getInterests().stream().map(Interest::getName).toList()
        ).forEach((metadataMap, userMetadata) -> filterMetadataMap(metadataMap, userMetadata, courseIds)
                .forEach((s, strings) -> {
                    map.put(s, strings);
                    courseIds.addAll(strings);
                }));
        user.getDepartmentCodes().forEach(dep -> {
            map.get(user.getOrganisationName()).addAll(map.getOrDefault(dep, List.of()));
            map.remove(dep);
        });
        return map;
    }

    private Map<String, Collection<String>> filterMetadataMap(Map<String, Collection<String>> map, Collection<String> metadata, Collection<String> courseIdsFilter) {
        LinkedHashMap<String, Collection<String>> result = new LinkedHashMap<>();
        metadata.forEach(m -> Optional.ofNullable(map.get(m))
                .ifPresent(courseIds -> {
                    courseIds = courseIds.stream().filter(courseId -> !courseIdsFilter.contains(courseId)).toList();
                    result.put(m, courseIds);
                    courseIdsFilter.addAll(courseIds);
                }));
        return result;
    }

}
