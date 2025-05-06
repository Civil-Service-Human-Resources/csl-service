package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;

import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class CourseRecordActionCollection {

    private final List<ICourseRecordAction> actions;
    private final List<CourseRecordId> courseRecordIds;

    public static CourseRecordActionCollection createWithSingleAction(ICourseRecordAction action) {
        return new CourseRecordActionCollection(List.of(action), List.of(action.getCourseRecordId()));
    }


    public CourseRecordActionCollectionResult process(Map<String, CourseRecord> courseRecordMap) {
        CourseRecordActionCollectionResult result = new CourseRecordActionCollectionResult();
        actions.forEach(action -> {
            CourseRecord courseRecord = courseRecordMap.get(action.getCourseRecordId().getAsString());
            if (courseRecord == null) {
                result.getNewRecords().add(action.generateNewCourseRecord());
            } else {
                result.getUpdatedRecords().put(courseRecord.getId(), action.applyUpdatesToCourseRecord(courseRecord));
            }
            result.getMessages().addAll(action.getMessages());
            result.getEmails().addAll(action.getEmails());
        });
        return result;
    }
}
