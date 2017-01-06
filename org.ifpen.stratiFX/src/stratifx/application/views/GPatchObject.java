package stratifx.application.views;

import java.util.ArrayList;
import java.util.List;

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.IPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;

public class GPatchObject extends GView  {
	
	boolean withPatchGrid = true;
		
	List<GPolyline> targets = new ArrayList<GPolyline>();
	
	public GPatchObject(){
		super("Interaction");
		setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );	
	}
	
	public void enableGrid( boolean withGrid ){
		withPatchGrid = withGrid;
	}
	
	@Override
	public void setModel(Object object) {
		// TODO Auto-generated method stub	
	}
	
	public void setDeformation(Deformation deformation ){
		
		for( GObject gobject : getChildren() ){
			if ( gobject instanceof IDeformableGeometry ){
				((IDeformableGeometry)gobject).setDeformation(deformation);
			}
		}
	}
	
	public void addInterval( PatchInterval interval ){
		
		GInterval gInterval = new GInterval(interval);
		add( gInterval );
		
		gInterval.draw();
	}
	
	
	public void addOutline( Patch patch, boolean surrounding ){
		
		GPolyline gBorder = new GPolyline(patch.getBorder());
		
		GStyle style = new GStyle();
		if ( !surrounding ){
			style.setBackgroundColor(GColor.CYAN);
		}
		else {
			style.setBackgroundColor(GColor.ORANGE);
			gBorder.enableDeformation(false);
		}
		gBorder.setStyle (style);
		
		add( gBorder );
	
		gBorder.draw();
				
		if ( withPatchGrid &&  !surrounding & ( patch instanceof IMeshProvider ) ){
				GMesh gMesh = new GMesh( ((IMeshProvider)patch).getMesh() );
				add( gMesh );
				
				gMesh.draw();
		}
		
	}
	
	
	public void clearTargets(){
		for ( GPolyline line : targets ){
			remove(line);
		}
		targets.clear();
	}

	public void addTarget(IPolyline targetLine) {
		
		GPolyline gline  = new GPolyline( targetLine );
		
		
		GStyle style = new GStyle();
		style.setForegroundColor( GColor.BLUE  );
		gline.setStyle( style );
		
		add( gline );
	
		targets.add( gline );		
		
		gline.draw();
	}
	
	
	@Override
	public void modelChanged(IControllerEvent<?> event) {		
		switch ((EnumEventAction) event.getEventAction()) {
		case MOVE:
			redraw();
			break;
		default:
			break;	 
		}
	}


}
