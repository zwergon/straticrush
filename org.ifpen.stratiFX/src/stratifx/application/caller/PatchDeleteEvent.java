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

public class PatchDeleteEvent extends AbstractControllerEvent<UnitRemovedItem> {

    public PatchDeleteEvent(final EnumEventAction action, final EnumEventType type,
            final UnitRemovedItem obj) {
        super(action, type, obj);
    }
}
