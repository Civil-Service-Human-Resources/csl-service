package uk.gov.cabinetoffice.csl.service.messaging;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.*;

import java.time.LocalDateTime;

@Service
public class MessageMetadataFactory {

    public CourseCompletionMessage generateCourseCompletionMessage(LocalDateTime completionDate, User user, Course course) {
        return new CourseCompletionMessage(completionDate, user.getId(), user.getEmail(), course.getId(), course.getTitle(),
                user.getOrganisationId(), user.getFormattedOrganisationName(), user.getProfessionId(),
                user.getProfessionName(), user.getGradeId(), user.getGradeName());
    }

    public CompleteProfileMessage generateCompleteProfileMessage(User user) {
        return new CompleteProfileMessage(new RegisteredLearnerProfile(user.getId(), user.getEmail(), user.getName(), user.getOrganisationId(),
                user.getFormattedOrganisationName(), user.getGradeId(), user.getGradeName(), user.getProfessionId(),
                user.getProfessionName()));
    }

    public UpdateProfileMessage generateUpdateProfileMessage(User user) {
        return new UpdateProfileMessage(new RegisteredLearnerProfile(user.getId(), user.getEmail(), user.getName(), user.getOrganisationId(),
                user.getFormattedOrganisationName(), user.getGradeId(), user.getGradeName(), user.getProfessionId(),
                user.getProfessionName()));
    }

    public RegisteredLearnerAccountActivateMessage generateRegisteredLearnerAccountActivateMessage(String uid) {
        return new RegisteredLearnerAccountActivateMessage(new RegisteredLearnerAccountActivate(uid, true));
    }

    public RegisteredLearnerEmailUpdateMessage generateRegisteredLearnerEmailUpdateMessage(String uid, String email) {
        return new RegisteredLearnerEmailUpdateMessage(new RegisteredLearnerEmailUpdate(uid, email));
    }

    public RegisteredLearnersOrganisationDeleteMessage generateRegisteredLearnersOrganisationDeleteMessage(Long organisationalUnitId) {
        return new RegisteredLearnersOrganisationDeleteMessage(new RegisteredLearnersOrganisationDelete(organisationalUnitId));
    }
}
