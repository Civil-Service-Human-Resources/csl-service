package uk.gov.cabinetoffice.csl.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public void completeProfile(@RequestParam(required = false, defaultValue = "false") boolean newProfile, @RequestBody List<Long> otherAreasOfWorkIds) {
        String uid = userAuthService.getUsername();
        userProfileService.setOtherAreasOfWork(uid, otherAreasOfWorkIds, newProfile);
    }

    @PostMapping(path = "/fullName")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void updateFullName(@RequestBody String fullName) {
        String uid = userAuthService.getUsername();
        userProfileService.setFullName(uid, fullName);
    }
}
