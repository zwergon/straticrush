package straticrush.caller;


import java.util.List;

import fr.ifp.kronosflow.controllers.AbstractControllerCaller;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.units.UnitController;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.geoscheduler.IGeoschedulerCaller;
import fr.ifp.kronosflow.model.Patch;
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

	@Override
	public void reApply() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void intializeFromStyle(Style style) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Style getStyle() {
		// TODO Auto-generated method stub
		return null;
	}
}
