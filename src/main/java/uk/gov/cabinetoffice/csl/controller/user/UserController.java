package uk.gov.cabinetoffice.csl.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.service.auth.IUserAuthService;
import uk.gov.cabinetoffice.csl.service.user.UserProfileService;

@RestController
@RequestMapping("/user/profile")
@Slf4j
public class UserController {

    private final IUserAuthService userAuthService;
    private final UserProfileService userProfileService;

    public UserController(IUserAuthService userAuthService, UserProfileService userProfileService) {
        this.userAuthService = userAuthService;
        this.userProfileService = userProfileService;
    }

    /*
    This should be reworked into a POST /other-areas-of-work endpoint. Other areas of work is the last required field
    for users, so we know that when they submit that then there's also a chance they'll be completing their profile.
     */
    @PostMapping(path = "/complete-profile")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void completeProfile() {
        String uid = userAuthService.getUsername();
        userProfileService.completeProfile(uid);
    }

}
