package uk.gov.cabinetoffice.csl.service.messaging;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.service.messaging.model.CourseCompletionMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.ActivateAccountMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.CompleteProfileMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerAccount;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.RegisteredLearnerProfile;

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

    public ActivateAccountMessage generateActivateAccountMessage(String uid) {
        return new ActivateAccountMessage(new RegisteredLearnerAccount(uid, true));
    }
}
