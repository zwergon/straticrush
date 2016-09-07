package straticrush.caller;


import java.util.List;

import fr.ifp.kronosflow.controllers.AbstractControllerCaller;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.units.UnitController;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.model.Patch;


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
    public void apply() {
    
        UnitController controller = getController();
        if ( (controller != null) && ( unitToRemove != null ) ) {
            removedPatchs = controller.removePatches(getService().getSection(), unitToRemove );
        }
    }

    @Override
    public void revert() {
        getController().addPatches(getService().getSection(), removedPatchs);
    }
}
