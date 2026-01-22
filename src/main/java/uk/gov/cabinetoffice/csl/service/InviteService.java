package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.controller.model.EmailAddressDto;
import uk.gov.cabinetoffice.csl.domain.identity.Identity;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.invite.InviteDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.service.learningCatalogue.LearningCatalogueService;
import uk.gov.cabinetoffice.csl.service.notification.INotificationService;
import uk.gov.cabinetoffice.csl.service.notification.NotificationFactory;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;

@RequiredArgsConstructor
@Service
public class InviteService {

    private final ILearnerRecordClient learnerRecordClient;
    private final LearningCatalogueService learningCatalogueService;
    private final UserDetailsService userDetailsService;
    private final NotificationFactory notificationFactory;
    private final INotificationService notificationService;

    public void inviteLearnerToEvent(String courseId, String moduleId, String eventId, EmailAddressDto emailAddressDto) {
        Identity identityDto = userDetailsService.getUserWithEmail(emailAddressDto.getLearnerEmail());
        CourseWithModuleWithEvent courseWithModuleWithEvent = learningCatalogueService.getCourseWithModuleWithEvent(courseId, moduleId, eventId);
        InviteDto invite = new InviteDto(emailAddressDto.getLearnerEmail(), identityDto.getUid());
        learnerRecordClient.createInvite(eventId, invite);
        notificationService.sendEmail(notificationFactory.getInviteUserToEventNotification(courseWithModuleWithEvent, emailAddressDto.getLearnerEmail()));
    }
}
