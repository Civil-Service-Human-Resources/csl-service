package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;

public interface ICSRSClient {

    CivilServant getCivilServantProfileWithUid(String uid);
}
