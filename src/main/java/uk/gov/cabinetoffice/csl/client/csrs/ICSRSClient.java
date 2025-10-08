package uk.gov.cabinetoffice.csl.client.csrs;
import uk.gov.cabinetoffice.csl.controller.model.OrganisationalUnitDto;

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

    UpdateDomainResponse addDomainToOrganisation(Long organisationalUnitId, String domain);

    UpdateDomainResponse deleteDomain(Long organisationUnitId, Long domainId, boolean includeSubOrganisations);

    void patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto);
}
