/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.caller;
import java.util.ArrayList;
import java.util.List;

import fr.ifp.kronosflow.controllers.AbstractChangeController;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.EnumEventType;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.algo.ComputeContact;
import fr.ifp.kronosflow.model.algo.KronosHelper;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;

public class UnitController extends AbstractChangeController {

	 public List<Patch> removePatches(Section section) {
		 return removePatches(section, section.getStratigraphicColumn().getFirstUnit() );
	 }

    public List<Patch> removePatches(Section section, StratigraphicUnit unit) {
        final List<Patch> patchList = new ArrayList<Patch>();

        if ( unit == null ){
        	return patchList;
        }

        PatchLibrary patchLib = section.getPatchLibrary();

        while (patchList.isEmpty() && unit != null) {
            for (Patch p : patchLib.getPatches()) {
                StratigraphicUnit feature = p
                        .getGeologicFeaturesByClass(StratigraphicUnit.class);
                if (feature != null && feature.equals(unit)) {
                    patchList.add(p);
                }
            }
            if (patchList.isEmpty()) {
                unit = unit.getBase() != null ? unit.getBase().getBelow() : null;
            }
        }

        if (unit != null) {
            PatchDeleteEvent event = new PatchDeleteEvent(EnumEventAction.REMOVE, EnumEventType.PATCH, new UnitRemovedItem(patchList, unit));
            push(event);
        }

        for (Patch patch : patchList) {
            ComputeContact.removeContactsOfPatch(patch, patchLib, patchLib.getContactGraph());
            patchLib.removePatch(patch);
        }

        KronosHelper.calculateBorders(patchLib);

        return patchList;
    }


    public void addPatches(Section section, List<Patch> patchList) {
        PatchAddEvent event = new PatchAddEvent(EnumEventAction.ADD, EnumEventType.PATCH, patchList);
        PatchLibrary patchLib = section.getPatchLibrary();
        for (Patch patch : patchList) {
        	patchLib.addPatch(patch);
            ComputeContact.recalculatePatch(patch, patchLib);
        }
        KronosHelper.calculateBorders(patchLib);
        push(event);
    }

}
