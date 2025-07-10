package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.domain.csrs.AreaOfWork;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.domain.csrs.PatchCivilServantDto;

import java.util.List;

public interface ICSRSClient {

    CivilServant getCivilServantProfileWithUid(String uid);

    List<OrganisationalUnit> getAllOrganisationalUnits();
    List<AreaOfWork> getAreasOfWork();

    void patchCivilServant(PatchCivilServantDto patch);
    List<OrganisationalUnit> getOrganisationalUnitsById(Integer[] ids, boolean fetchChildren);
}
