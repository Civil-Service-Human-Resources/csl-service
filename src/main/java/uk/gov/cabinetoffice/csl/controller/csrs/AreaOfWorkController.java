package uk.gov.cabinetoffice.csl.controller.csrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.domain.csrs.AreasOfWork;
import uk.gov.cabinetoffice.csl.service.csrs.CivilServantRegistryService;

@Slf4j
@RestController
@RequestMapping("/areas-of-work")
public class AreaOfWorkController {

    private final CivilServantRegistryService civilServantRegistryService;

    public AreaOfWorkController(CivilServantRegistryService civilServantRegistryService) {
        this.civilServantRegistryService = civilServantRegistryService;
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public AreasOfWork getAllProfessions() {
        log.info("Getting all professions");
        return new AreasOfWork(civilServantRegistryService.getAreasOfWork());

    }
}
