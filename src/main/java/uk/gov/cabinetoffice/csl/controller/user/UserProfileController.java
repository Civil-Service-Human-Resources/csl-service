package uk.gov.cabinetoffice.csl.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.model.FullNameParam;
import uk.gov.cabinetoffice.csl.controller.model.GradeParam;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitIdParam;
import uk.gov.cabinetoffice.csl.controller.model.ProfessionParam;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.user.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/user/profile")
@Slf4j
public class UserProfileController {

    private final IUserAuthService userAuthService;
    private final UserProfileService userProfileService;

    public UserProfileController(IUserAuthService userAuthService, UserProfileService userProfileService) {
        this.userAuthService = userAuthService;
        this.userProfileService = userProfileService;
    }

    @PostMapping(path = "/other-areas-of-work")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void updateOtherAreasOfWork(@RequestBody List<Long> otherAreasOfWorkIds) {
        String uid = userAuthService.getUsername();
        userProfileService.setOtherAreasOfWork(uid, otherAreasOfWorkIds);
    }

    @PostMapping(path = "/full-name")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void updateFullName(@RequestParam(required = false, defaultValue = "false") boolean newProfile, @RequestBody FullNameParam fullNameUpdateRequest) {
        log.debug("UserProfileController: fullNameUpdateRequest: {}", fullNameUpdateRequest);
        String uid = userAuthService.getUsername();
        userProfileService.setFullName(uid, fullNameUpdateRequest.getFullName(), newProfile);
    }

    @PostMapping(path = "/grade")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void updateGrade(@RequestBody GradeParam gradeUpdateRequest) {
        log.debug("UserProfileController: gradeUpdateRequest: {}", gradeUpdateRequest);
        String uid = userAuthService.getUsername();
        userProfileService.setGrade(uid, gradeUpdateRequest.getGradeId());
    }

    @PostMapping(path = "/profession")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void updateProfession(@RequestBody ProfessionParam professionUpdateRequest) {
        log.debug("UserProfileController: professionUpdateRequest: {}", professionUpdateRequest);
        String uid = userAuthService.getUsername();
        userProfileService.setProfession(uid, professionUpdateRequest.getProfessionId());
    }

    @PostMapping(path = "/organisationUnit")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void updateOrganisationUnit(@RequestBody OrganisationalUnitIdParam organisationUnitUpdateRequest) {
        log.debug("UserProfileController: organisationUnitUpdateRequest: {}", organisationUnitUpdateRequest);
        String uid = userAuthService.getUsername();
        userProfileService.setOrganisationalUnit(uid, organisationUnitUpdateRequest.getOrganisationUnitId());
    }
}
