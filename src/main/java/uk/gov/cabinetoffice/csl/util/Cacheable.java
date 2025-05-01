package uk.gov.cabinetoffice.csl.util;

import java.io.Serializable;

public interface Cacheable extends Serializable {
    String getCacheableId();
}
