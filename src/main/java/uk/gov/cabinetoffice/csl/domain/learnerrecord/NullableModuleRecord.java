package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import uk.gov.cabinetoffice.csl.util.NullableCacheObject;

public class NullableModuleRecord extends NullableCacheObject<ModuleRecord> {

    public NullableModuleRecord(String cacheableId, ModuleRecord object) {
        super(cacheableId, object);
    }
}
