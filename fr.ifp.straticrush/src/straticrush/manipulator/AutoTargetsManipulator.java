package straticrush.manipulator;

import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import straticrush.interaction.StratiCrushServices;
import fr.ifp.jdeform.deformation.constraint.LinePairingItem;
import fr.ifp.jdeform.deformation.constraint.PatchIntersectionItem;
import fr.ifp.kronosflow.geology.FaultFeature;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.IPolyline;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.algo.LineIntersection;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;

public class AutoTargetsManipulator  extends CompositeManipulator {
	
	
	protected PatchInterval  selectedHorizon = null;
	
	protected PatchInterval  selectedFault = null;
	

	public AutoTargetsManipulator( GScene scene, Patch selectedComposite, List<Patch> surroundedComposites ){
		 super( scene, selectedComposite, surroundedComposites );
	}
	
	@Override
	public void onMousePress( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		double[] wc = scene.getTransformer().deviceToWorld(event.x, event.y);
		selectedHorizon = findHorizonFeature( wc );
		selectedFault   = findFaultFeature( wc );

		
		if ( null != selectedHorizon ){
			selectedHorizon.getInterval().createExtension();	
			interaction.addInterval(selectedHorizon);
		}
		
		if ( null != selectedFault ){
			interaction.addInterval(selectedFault);
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
			
			ExplicitPolyLine faultTarget = createDefaultTarget(I);
			item = new LinePairingItem( selectedFault.getInterval(),  faultTarget );
			items.add( item );
			
			interaction.addLine( faultTarget );
		}
		
		interaction.draw();
		
		
	}

	@Override
	public void onMouseRelease( GMouseEvent event ){
		
	}
	

	
	/**
	 * Create a vertical {@link IPolyline} from intersection point I to bottom
	 * of selectedComposite {@link Patch}
	 */
	private ExplicitPolyLine createDefaultTarget(LinePointPair I) {
		
		double length = 1.5*selectedComposite.getBorder().getBoundingBox().height();
		List<Point2D> pts = new ArrayList<Point2D>();
		Point2D origin = I.getPoint().getPosition();
		Point2D target = new Point2D(origin);
		
		origin.setY( origin.y() + length );
		
		target.setY( target.y() - length );
		pts.add( origin );
		pts.add( target );
		
		ExplicitPolyLine faultTarget = new ExplicitPolyLine( pts );
		return faultTarget;
	}



	
	

}
