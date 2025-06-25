package uk.gov.cabinetoffice.csl.service.user;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.csrs.ICSRSClient;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.AreaOfWork;
import uk.gov.cabinetoffice.csl.domain.csrs.PatchCivilServantDto;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.CompleteProfileMessage;

import java.util.List;

@Service
public class UserProfileService {

    private final MessageMetadataFactory messageMetadataFactory;
    private final UserDetailsService userDetailsService;
    private final ICSRSClient client;
    private final IMessagingClient messagingClient;

    public UserProfileService(MessageMetadataFactory messageMetadataFactory, UserDetailsService userDetailsService,
                              ICSRSClient client, IMessagingClient messagingClient) {
        this.messageMetadataFactory = messageMetadataFactory;
        this.userDetailsService = userDetailsService;
        this.client = client;
        this.messagingClient = messagingClient;
    }

    public void setOtherAreasOfWork(String uid, List<Long> otherAreasOfWorkIds, boolean newProfile) {
        List<AreaOfWork> areasOfWork = client.getAreasOfWork()
                .stream().flatMap(p -> p.getFlat().stream())
                .filter(p -> otherAreasOfWorkIds.contains(p.getId())).toList();
        if (!areasOfWork.isEmpty()) {
            PatchCivilServantDto patch = PatchCivilServantDto.builder().otherAreasOfWork(areasOfWork).build();
            client.patchCivilServant(uid, patch);
            if (newProfile) {
                User user = userDetailsService.getUserWithUid(uid);
                CompleteProfileMessage message = messageMetadataFactory.generateCompleteProfileMessage(user);
                messagingClient.sendMessages(List.of(message));
            }
        }
    }
}
