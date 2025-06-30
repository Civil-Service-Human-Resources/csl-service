package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.event.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.CompleteModule;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.LaunchModule;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.actions.module.RollupModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.domain.rustici.CSLRusticiProps;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.time.LocalDateTime;

@Service
public class ModuleRecordActionFactory {

    private final IUtilService iUtilService;

    public ModuleRecordActionFactory(IUtilService iUtilService) {
        this.iUtilService = iUtilService;
    }

    public LaunchModule getLaunchModuleAction() {
        return new LaunchModule();
    }

    public CompleteModule getCompleteModuleAction() {
        return getCompleteModuleAction(iUtilService.getNowDateTime());
    }

    public CompleteModule getCompleteModuleAction(LocalDateTime completionDate) {
        return new CompleteModule(completionDate);
    }

    public RollupModule getRollUpModuleAction(CSLRusticiProps properties) {
        return new RollupModule(properties);
    }

    public RegisterEvent getRegisterEventAction(Event event) {
        return new RegisterEvent(event);
    }

    public CancelBooking getCancelBookingAction() {
        return new CancelBooking();
    }

    public ApproveBooking getApproveBookingAction(Event event) {
        return new ApproveBooking(event);
    }

    public CompleteBooking getCompleteBookingAction() {
        return new CompleteBooking(iUtilService.getNowDateTime());
    }

    public SkipBooking getSkipBookingAction() {
        return new SkipBooking();
    }


}
