package uk.gov.cabinetoffice.csl.util;

import java.io.Serializable;

public interface IFetchClient<T extends Serializable> {

    T fetch();

}
