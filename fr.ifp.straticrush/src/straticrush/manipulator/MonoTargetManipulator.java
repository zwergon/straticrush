package straticrush.manipulator;

import java.util.List;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import fr.ifp.jdeform.deformation.constraint.LinePairingItem;
import fr.ifp.jdeform.deformation.constraint.PatchIntersectionItem;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.algo.LineIntersection;

public class MonoTargetManipulator extends CompositeManipulator  {
	
	protected PatchInterval  selectedHorizon = null;

	public MonoTargetManipulator( GScene scene, Patch selectedComposite, List<Patch> surroundedComposites ){
		 super( scene, selectedComposite, surroundedComposites );
	}
	
	@Override
	public void onMousePress( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		double[] wc = scene.getTransformer().deviceToWorld(event.x, event.y);
		selectedHorizon = findHorizonFeature( wc );
		
		
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
		
		
		interaction.clearLines();
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
		
		
	}

	@Override
	public void onMouseRelease( GMouseEvent event ){
		
	}
	

}
