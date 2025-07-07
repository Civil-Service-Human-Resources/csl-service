package uk.gov.cabinetoffice.csl.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.service.user.UserAccountService;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping(path = "/{uid}/activate")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void activate(@PathVariable String uid) {
        log.info("activate uid: {}", uid);
        userAccountService.activateUser(uid);
    }
}
