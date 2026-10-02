package uk.gov.cabinetoffice.csl.service.learning;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.EventStatus;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.FaceToFaceModuleOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.FileModuleOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.ModuleOverview;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.overview.ModuleOverviewCollection;
import uk.gov.cabinetoffice.csl.util.IUtilService;
import uk.gov.cabinetoffice.csl.util.TuplePair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LearningOverviewFactory {

    private final IUtilService utilService;

    public LearningOverviewFactory(IUtilService utilService) {
        this.utilService = utilService;
    }

    public FaceToFaceModuleOverview getFaceToFaceModuleOverview(Module module, String bookedEventId, State state) {
        boolean canBeBooked = false;
        boolean canBeCancelled = false;
        if (bookedEventId != null) {
            canBeCancelled = Optional.ofNullable(module.getEvent(bookedEventId))
                    .map(event -> !List.of(State.UNREGISTERED, State.NULL).contains(state) && event.getStatus().equals(EventStatus.ACTIVE))
                    .orElse(false);
        } else {
            canBeBooked = module.canBeBooked(utilService.getNowDateTime());
        }
        return new FaceToFaceModuleOverview(module.getId(), module.getTitle(), module.getDescription(), !module.isOptional(), module.isAssociatedLearning(), module.getModuleType(),
                module.getDurationInSeconds(), module.getCost().intValue(), state, false, bookedEventId, canBeBooked, canBeCancelled);
    }

    public FileModuleOverview getFileModuleOverview(Module module, State state) {
        TuplePair<String, String> filenameAndExt = utilService.getFilenameAndExt(module.getUrl());
        return new FileModuleOverview(module.getId(), module.getTitle(), module.getDescription(), !module.isOptional(), module.isAssociatedLearning(), module.getModuleType(),
                module.getDurationInSeconds(), module.getCost().intValue(), state, false, filenameAndExt.a(), filenameAndExt.b(), module.getFileSize());
    }

    public ModuleOverviewCollection getModuleOverviews(Collection<Module> modules, Map<String, ModuleRecord> moduleRecordMap,
                                                       Optional<LearningPeriod> optLp) {
        ModuleOverviewCollection collection = new ModuleOverviewCollection();
        modules.forEach(module -> {
            State state = State.NULL;
            ModuleRecord moduleRecord = moduleRecordMap.get(module.getId());
            if (moduleRecord != null) {
                state = optLp.map(moduleRecord::getStateForLearningPeriod).orElse(moduleRecord.getState());
            }
            ModuleOverview o = switch (module.getModuleType()) {
                case facetoface -> {
                    collection.setHasFaceToFace(true);
                    String eventId = moduleRecord == null ? null : moduleRecord.getEventId();
                    yield getFaceToFaceModuleOverview(module, eventId, state);
                }
                case file -> getFileModuleOverview(module, state);
                default ->
                        new ModuleOverview(module.getId(), module.getTitle(), module.getDescription(), !module.isOptional(), module.isAssociatedLearning(), module.getModuleType(),
                                module.getDurationInSeconds(), module.getCost().intValue(), state, module.isAssociatedLearning());
            };
            collection.add(o);
            if (!module.isOptional()) collection.setMandatoryCount(collection.getMandatoryCount() + 1);
        });
        collection.setAssociatedLearning();
        return collection;
    }


}
