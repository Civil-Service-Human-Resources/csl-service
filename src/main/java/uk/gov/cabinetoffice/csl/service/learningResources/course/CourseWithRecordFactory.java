package uk.gov.cabinetoffice.csl.service.learningResources.course;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseWithRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ILearnerRecord;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

@Service
public class CourseWithRecordFactory {
    public CourseWithRecord build(Course course, String learnerId, ILearnerRecord record) {
        return new CourseWithRecord(course.getId(), course.getTitle(), course.getShortDescription(), course.getModules(),
                course.getAudiences(), course.getDepartmentCodeToRequiredAudienceMap(), learnerId, record);
    }

}
