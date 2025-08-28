package uk.gov.cabinetoffice.csl.client.csrs;
import uk.gov.cabinetoffice.csl.domain.csrs.*;

import java.util.List;

public interface ICSRSClient {

    CivilServant getCivilServantProfileWithUid(String uid);

    OrganisationalUnitMap getAllOrganisationalUnits();

    List<AreaOfWork> getAreasOfWork();

    List<Grade> getGrades();

    void patchCivilServant(PatchCivilServantDto patch);

    void patchCivilServantOrganisation(OrganisationDTO organisationDTO);

    void deleteOrganisationalUnit(OrganisationDTO organisationDTO);
}
