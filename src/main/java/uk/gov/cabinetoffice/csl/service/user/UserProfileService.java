package uk.gov.cabinetoffice.csl.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;
import uk.gov.cabinetoffice.csl.service.messaging.IMessagingClient;
import uk.gov.cabinetoffice.csl.service.messaging.MessageMetadataFactory;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.CompleteProfileMessage;
import uk.gov.cabinetoffice.csl.service.messaging.model.registeredLearners.UpdateProfileMessage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserProfileService {
    private final UserDetailsService userDetailsService;
    private final CivilServantRegistryService civilServantRegistryService;
    private final MessageMetadataFactory messageMetadataFactory;
    private final IMessagingClient messagingClient;

    public UserProfileService(UserDetailsService userDetailsService,
                              CivilServantRegistryService civilServantRegistryService,
                              MessageMetadataFactory messageMetadataFactory,
                              IMessagingClient messagingClient) {
        this.userDetailsService = userDetailsService;
        this.civilServantRegistryService = civilServantRegistryService;
        this.messageMetadataFactory = messageMetadataFactory;
        this.messagingClient = messagingClient;
    }

    public void setOtherAreasOfWork(String uid, List<Long> otherAreasOfWorkIds) {
        List<AreaOfWork> areasOfWork = civilServantRegistryService.getAreasOfWork()
                .stream().flatMap(p -> p.getFlat().stream())
                .filter(p -> otherAreasOfWorkIds.contains(p.getId())).toList();
        if (!areasOfWork.isEmpty()) {
            PatchCivilServantDto patch = PatchCivilServantDto.builder().otherAreasOfWork(areasOfWork).build();
            User user = patchCivilServant(patch, uid);
            updateReportingData(user);
        }
    }

    public void setFullName(String uid, String fullName, boolean newProfile) {
        PatchCivilServantDto patch = PatchCivilServantDto.builder().fullName(fullName).build();
        User user = patchCivilServant(patch, uid);
        if (newProfile) {
            createReportingData(user);
        } else {
            updateReportingData(user);
        }
    }

    public void setGrade(String uid, Long gradeId) {
        Optional<Grade> optGrade = civilServantRegistryService.getGrades()
                .stream()
                .filter(g -> g.getId().equals(gradeId))
                .findFirst();
        if (optGrade.isPresent()) {
            PatchCivilServantDto patch = PatchCivilServantDto.builder().grade(optGrade.get()).build();
            User user = patchCivilServant(patch, uid);
            updateReportingData(user);
        }
    }

    public void setProfession(String uid, Long professionId) {
        Optional<AreaOfWork> optAreaOfWork = civilServantRegistryService.getAreasOfWork()
                .stream()
                .filter(g -> g.getId().equals(professionId))
                .findFirst();
        if (optAreaOfWork.isPresent()) {
            PatchCivilServantDto patch = PatchCivilServantDto.builder().profession(optAreaOfWork.get()).build();
            User user = patchCivilServant(patch, uid);
            updateReportingData(user);
        }
    }

    public void setOrganisationalUnit(String uid, Long organisationalUnitId) {
        User user = patchCivilServantOrganisation(uid, organisationalUnitId);
        updateReportingData(user);
    }

    private User patchCivilServantOrganisation(String uid, Long organisationalUnitId) {
        civilServantRegistryService.patchCivilServantOrganisation(new UpdateOrganisationDTO(organisationalUnitId));
        userDetailsService.removeUserFromCache(uid);
        return userDetailsService.getUserWithUid(uid);
    }

    private User patchCivilServant(PatchCivilServantDto patch, String uid) {
        civilServantRegistryService.patchCivilServant(patch);
        userDetailsService.removeUserFromCache(uid);
        return userDetailsService.getUserWithUid(uid);
    }

    private void createReportingData(User user) {
        log.debug("createReportingData:user: {}", user);
        CompleteProfileMessage message = messageMetadataFactory.generateCompleteProfileMessage(user);
        messagingClient.sendMessages(List.of(message));
    }

    private void updateReportingData(User user) {
        log.debug("updateReportingData:user: {}", user);
        UpdateProfileMessage message = messageMetadataFactory.generateUpdateProfileMessage(user);
        messagingClient.sendMessages(List.of(message));
    }
}
