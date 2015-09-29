package straticrush.manipulator;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.deformation.items.LinePairingItem;
import fr.ifp.jdeform.deformation.items.PatchIntersectionItem;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class MonoTargetManipulator extends CompositeManipulator  {
	
	protected PatchInterval  selectedHorizon = null;
	
	double[] start;
	
	double[] prev;

	public MonoTargetManipulator( GScene scene, DeformationControllerCaller caller ){
		 super( scene, caller );
	}
	
	@Override
	public void onMousePress( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		start = scene.getTransformer().deviceToWorld(event.x, event.y);
		prev  = Vector2D.copy(start);
		
		selectedHorizon = findHorizonFeature( start );
		
		
		if ( null != selectedHorizon ){
			selectedHorizon.getInterval().createExtension();	
			interaction.addInterval(selectedHorizon);
		}
		
	}
	
	@Override
	public void onMouseMove( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		
		double[] wc = scene.getTransformer().deviceToWorld(event.x, event.y);
		
		double[] t = Vector2D.substract(wc, prev);
		
		translateTo(t);

		interaction.clearLines();
		
		Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
		LineIntersection lineInter = new LineIntersection( bathy.getPolyline() );
		
		LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
		if ( null != I ) {
			interaction.addLine( I.getPoint().getLine() );
		}
		
		interaction.draw();
		
		prev = wc;
	}

	@Override
	public void onMouseRelease( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		items.clear();
		
		Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
		LineIntersection lineInter = new LineIntersection( bathy.getPolyline() );
		
		LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
		if ( null != I ) {
			LinePairingItem item = new PatchIntersectionItem( selectedHorizon, I );
			items.add( item );
			interaction.addLine( item.getMateLine() );
		}	
		
		interaction.draw();
		
		
		double[] t = Vector2D.substract(start, prev);
		translateTo(t);
	}
	

}
