package stratifx.application.caller;


import java.util.List;

import fr.ifp.kronosflow.controllers.AbstractControllerCaller;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.units.UnitController;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;
import fr.ifp.kronosflow.model.style.Style;


public class RemoveUnitCaller 
	extends 
	AbstractControllerCaller<UnitController> 
	implements
        IGeoschedulerCaller<UnitController> {

    private List<Patch> removedPatchs;
    
    StratigraphicUnit unitToRemove;

    public RemoveUnitCaller(IControllerService service) {
        super(service, new UnitController());
    }
    
    public void setUnitToRemove( StratigraphicUnit unit ){
    	unitToRemove = unit;
    }

    @Override
    public void compute() {
    
        UnitController controller = getController();
        if ( (controller != null) && ( unitToRemove != null ) ) {
            removedPatchs = controller.removePatches(getService().getSection(), unitToRemove );
        }
    }

    @Override
    public Style getStyle() {
        // TODO Auto-generated method stub
        return null;
    }

	

}
