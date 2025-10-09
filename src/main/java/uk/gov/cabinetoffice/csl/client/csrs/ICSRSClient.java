package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.controller.csrs.model.CreateDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.DeleteDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.domain.csrs.*;

import java.util.List;

public interface ICSRSClient {

    CivilServant getCivilServantProfileWithUid(String uid);

    OrganisationalUnitMap getAllOrganisationalUnits();

    List<AreaOfWork> getAreasOfWork();

    List<Grade> getGrades();

    void patchCivilServant(PatchCivilServantDto patch);

    void patchCivilServantOrganisation(OrganisationalUnitIdDto organisationalUnitIdDTO);

    void deleteOrganisationalUnit(Long organisationalUnitId);

    UpdateDomainResponse addDomainToOrganisation(Long organisationalUnitId, CreateDomainDto domain);

    UpdateDomainResponse deleteDomain(Long organisationUnitId, Long domainId, DeleteDomainDto dto);

    void patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto);
}
