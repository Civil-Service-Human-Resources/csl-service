package uk.gov.cabinetoffice.csl.util;

import org.springframework.cache.Cache;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.NullableModuleRecord;

public class ModuleRecordCache extends NullableObjectCache<ModuleRecord, NullableModuleRecord> {

    public ModuleRecordCache(Cache cache, Class<NullableModuleRecord> clazz) {
        super(cache, clazz);
    }

    @Override
    NullableModuleRecord createCacheObject(String id, ModuleRecord value) {
        return new NullableModuleRecord(id, value);
    }

}
