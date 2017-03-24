/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.caller;


import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;
import java.util.Collections;
import java.util.List;

public class UnitRemovedItem {

    private List<Patch> patches;

    private StratigraphicUnit unitDeleted;

    public UnitRemovedItem(List<Patch> patches, StratigraphicUnit unitDeleted) {
        this.patches = patches;
        this.unitDeleted = unitDeleted;
    }

    public List<Patch> getPatches() {
        return Collections.unmodifiableList(patches);
    }

    public StratigraphicUnit getUnitDeleted() {
        return unitDeleted;
    }
}
