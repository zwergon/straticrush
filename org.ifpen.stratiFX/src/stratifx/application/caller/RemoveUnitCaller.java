/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.application.caller;


import java.util.List;

import fr.ifp.kronosflow.controllers.AbstractControllerCaller;
import fr.ifp.kronosflow.controllers.IControllerService;
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

    @Override
    public void intializeFromStyle(Style style) {
        //do nothing
    }

    @Override
    public void updateStyle() {
        //do nothing
    }

	

}
