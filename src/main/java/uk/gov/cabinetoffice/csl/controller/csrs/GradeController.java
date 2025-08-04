package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.csrs.Grades;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

@Slf4j
@RestController
@RequestMapping("/grades")
public class GradeController {

    private final CivilServantRegistryService civilServantRegistryService;

    public GradeController(CivilServantRegistryService civilServantRegistryService) {
        this.civilServantRegistryService = civilServantRegistryService;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Grades getGrades() {
        log.info("Getting grades");
        return new Grades(civilServantRegistryService.getGrades());
    }
}
