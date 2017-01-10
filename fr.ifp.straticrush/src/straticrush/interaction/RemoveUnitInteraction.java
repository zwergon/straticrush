package straticrush.interaction;

import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import straticrush.caller.RemoveUnitCaller;
import straticrush.view.PatchView;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerLink;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;


public class RemoveUnitInteraction implements GInteraction {
	
	private GScene    gscene;
	protected RemoveUnitCaller caller;
	
	public RemoveUnitInteraction( GScene scene, String type ){
		gscene = scene; 
		caller =  (RemoveUnitCaller)StratiCrushServices.getInstance().createCaller("RemoveUnit") ;
	}
	
	public RemoveUnitCaller getCaller(){
		return caller;
	}

	@Override
	public void event(GScene gscene, GMouseEvent event) {
		if ( this.gscene != gscene ){
			return;
		}

		switch (event.type) {
		case GMouseEvent.BUTTON1_DOWN :
			GSegment selected = gscene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
				
					Patch patch = ((PatchView)gobject).getObject();
					
				
					StratigraphicUnit unit = patch.getGeologicFeaturesByClass(StratigraphicUnit.class);
					
					if ( unit != null ){
						getCaller().setUnitToRemove(unit);
					}
					
				
					gscene.refresh();
				}
			}
			break;

		case GMouseEvent.BUTTON1_DRAG :
			break;

		case GMouseEvent.BUTTON1_UP :
			getCaller().applyAndNotify();
			gscene.refresh();
			break;
			
		case GMouseEvent.ABORT:
			break;
		}
		
		gscene.refresh();

		
	}

	@Override
	public void keyEvent(GKeyEvent event) {
		// TODO Auto-generated method stub
		
	}

}
