package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.domain.csrs.AreaOfWork;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnitMap;
import uk.gov.cabinetoffice.csl.domain.csrs.PatchCivilServantDto;

import java.util.List;

public interface ICSRSClient {

    CivilServant getCivilServantProfileWithUid(String uid);

    OrganisationalUnitMap getAllOrganisationalUnits();

    List<AreaOfWork> getAreasOfWork();

    void patchCivilServant(PatchCivilServantDto patch);
}
