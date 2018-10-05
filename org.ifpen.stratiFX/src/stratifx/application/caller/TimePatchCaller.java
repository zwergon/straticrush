package stratifx.application.caller;


import fr.ifp.kronosflow.controllers.AbstractControllerCaller;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.events.AbstractControllerEvent;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.EnumEventType;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.StratigraphicColumn;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.utils.Pair;
import fr.ifp.kronosflow.utils.LOGGER;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimePatchCaller
        extends
        AbstractControllerCaller<SplitPatchController>
        implements
        IGeoschedulerCaller<SplitPatchController> {

    Map<Patch, PolyLine> patchPolyLineMap = new HashMap<>();

    double time;

    public TimePatchCaller(IControllerService service) {
        super(service, new SplitPatchController());
    }

    public void setTime( double time ){
        this.time = time;
    }

    public void addPatchToSplit(Patch patch, PolyLine line){
        patchPolyLineMap.put(patch, line);
    }

    @Override
    public void compute() {

        SplitPatchController controller = getController();

        StratigraphicEvent stratigraphicEvent = findOrCreateEvent(time);
        if ( null == stratigraphicEvent ){
            return;
        }

        if ( (controller != null) && ( !patchPolyLineMap.isEmpty()  ) ) {
            List<Patch> toRemove = new ArrayList<>();
            List<Patch> toAdd = new ArrayList<>();
            for(Map.Entry<Patch, PolyLine> entry : patchPolyLineMap.entrySet() ){
                Patch oldPatch = entry.getKey();
                PolyLine line = entry.getValue();
                toRemove.add(oldPatch);
                Pair<Patch,Patch> addedPair = controller.splitPatch(oldPatch, line, stratigraphicEvent);
                toAdd.add(addedPair.getFirst());
                toAdd.add(addedPair.getSecond());
            }

            AbstractControllerEvent<?> event = new PatchDeleteEvent(EnumEventAction.REMOVE,
                    EnumEventType.PATCH, new UnitRemovedItem(toRemove, null));
            controller.push(event);

            event = new PatchAddEvent(EnumEventAction.ADD, EnumEventType.PATCH, toAdd );
            controller.push(event);
        }
    }

    @Override
    public Style getStyle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void intializeFromStyle(Style style) {
        //do nothing
    }

    @Override
    public void updateStyle() {
        //do nothing
    }

    private StratigraphicEvent findOrCreateEvent(double time) {

        Section section = getService().getSection();
        StratigraphicColumn column = section.getStratigraphicColumn();

        for( StratigraphicEvent event : column.events() ){
            if ( event.isDated() && event.getAge() == time ){
                return event;
            }
        }


        StratigraphicUnit unit = null;
        for( StratigraphicUnit u : column.units() ){
            StratigraphicEvent uTop = u.getTop();
            StratigraphicEvent uBase = u.getBase();

            if ( !uTop.isDated() || !uBase.isDated() ){
                LOGGER.warning(String.format("Unit %s as no age ", u.getName()), getClass());
                continue;
            }

            if ( (uTop.getAge() - time)*(uBase.getAge()-time) < 0 ){
                unit = u;
                break;
            }
        }

        if ( unit ==  null ){
            LOGGER.warning(String.format("Unable to add horizon with time ", time), getClass());
            return null;
        }


        StratigraphicEvent newHorizon = new StratigraphicEvent();
        newHorizon.setAge(time);
        newHorizon.setName( "Sub" + unit.getTop().getName() );
        StratigraphicUnit newUnit = column.insertBelow( unit, newHorizon);

        Color oldColor = unit.getColor();
        newUnit.setColor( oldColor.brighter() ) ;

        System.out.println(column);

        return newHorizon;
    }


}
