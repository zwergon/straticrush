/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.caller;

import fr.ifp.kronosflow.controllers.events.AbstractControllerEvent;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.EnumEventType;
import fr.ifp.kronosflow.model.Patch;
import java.util.List;

public class PatchAddEvent extends AbstractControllerEvent<List<Patch>> {
    public PatchAddEvent(final EnumEventAction action, final EnumEventType type,
            final List<Patch> obj) {
        super(action, type, obj);
    }
}
