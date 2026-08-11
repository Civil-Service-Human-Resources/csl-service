package uk.gov.cabinetoffice.csl.config.redis;

import org.springframework.cache.Cache;

public interface ITtlCache extends Cache {

    void put(Object key, Object value, Long ttlSeconds);

}
