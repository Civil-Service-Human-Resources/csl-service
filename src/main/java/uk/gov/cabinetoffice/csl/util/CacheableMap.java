package uk.gov.cabinetoffice.csl.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CacheableMap<T extends Serializable> implements Serializable {

    protected Map<String, T> map;
    protected String cacheableId;
    
}
