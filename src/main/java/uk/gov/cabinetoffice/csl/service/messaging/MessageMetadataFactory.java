package uk.gov.cabinetoffice.csl.service.messaging;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;

import java.time.LocalDateTime;

@Service
public class MessageMetadataFactory {

    public CourseCompletionMessage generateCourseCompletionMessage(LocalDateTime completionDate, User user, Course course) {
        return new CourseCompletionMessage(completionDate, user.getId(), user.getEmail(), course.getId(), course.getTitle(),
                user.getOrganisationId(), user.getFormattedOrganisationName(), user.getProfessionId(),
                user.getProfessionName(), user.getGradeId(), user.getGradeName());
    }

}
