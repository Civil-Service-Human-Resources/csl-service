package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module;

import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.IModuleRecordAction;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.MultiCourseRecordAction;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.util.UtilService;

public class MultiModuleRecordActionProcessor extends ModuleRecordActionProcessor {

    private final MultiCourseRecordAction actionTypes;

    public MultiModuleRecordActionProcessor(UtilService utilService, CourseWithModule courseWithModule,
                                            User user, MultiCourseRecordAction actionTypes) {
        super(utilService, courseWithModule, user, actionTypes);
        this.actionTypes = actionTypes;
    }

    @Override
    public ModuleRecord applyUpdatesToModuleRecord(ModuleRecord moduleRecord) {
        for (IModuleRecordAction action : actionTypes.getActions()) {
            moduleRecord = action.applyUpdatesToModuleRecord(moduleRecord);
        }
        return moduleRecord;
    }

}
