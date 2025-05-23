package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.LearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Service
public class CourseWithRecordFactory {
    public CourseWithRecord build(String learnerId, Course course, LearnerRecord record) {
        return new CourseWithRecord(learnerId, course.getId(), course.getTitle(), course.getModules(), record);
    }

}
