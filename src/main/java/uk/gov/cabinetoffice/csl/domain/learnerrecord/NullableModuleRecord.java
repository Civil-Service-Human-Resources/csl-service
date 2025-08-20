package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import uk.gov.cabinetoffice.csl.util.NullableCacheObject;

public class NullableModuleRecord extends NullableCacheObject<ModuleRecord> {
    public NullableModuleRecord(ModuleRecord object) {
        super(object);
    }
}
