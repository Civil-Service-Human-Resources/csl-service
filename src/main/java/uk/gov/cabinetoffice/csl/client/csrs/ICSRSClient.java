package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.List;

public interface ICSRSClient {

    CivilServant getCivilServantProfileWithUid(String uid);

    List<OrganisationalUnit> getAllOrganisationalUnits();
}
