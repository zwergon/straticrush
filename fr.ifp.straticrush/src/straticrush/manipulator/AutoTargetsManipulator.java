package straticrush.manipulator;

import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GScene;
import fr.ifp.jdeform.controllers.callers.DeformationControllerCaller;
import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.deformation.IDeformationItem;
import fr.ifp.jdeform.deformation.IRigidItem;
import fr.ifp.jdeform.deformation.items.LinePairingItem;
import fr.ifp.jdeform.deformation.items.PatchIntersectionItem;
import fr.ifp.jdeform.deformation.items.TranslateItem;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.LineIntersection;
import fr.ifp.kronosflow.polyline.LinePointPair;

public class AutoTargetsManipulator  extends CompositeManipulator {
	
	
	protected PatchInterval  selectedHorizon = null;
	
	protected PatchInterval  selectedFault = null;
	
	protected ExplicitPolyLine faultTarget;
	

	public AutoTargetsManipulator( GScene gscene, DeformationControllerCaller caller ){
		 super( gscene, caller );
	}
	
	@Override
	public void onMousePress( GMouseEvent event ){
		
		if ( !isActive() ){
			return;
		}
		
		super.onMousePress(event);
		
		double[] wc = gscene.getTransformer().deviceToWorld(event.x, event.y);
		selectedHorizon = findHorizonFeature( wc );
		selectedFault   = findFaultFeature( wc );

		
		if ( null != selectedHorizon ){
			selectedHorizon.getInterval().createExtension();	
			selectedPatchGraphic.addInterval(selectedHorizon);
		}
		
		if ( null != selectedFault ){
			selectedPatchGraphic.addInterval(selectedFault);
		}
		
	}

	@Override
	public void onMouseRelease( GMouseEvent event ){
		
	}
	
	@Override
	public boolean canDeform() {
		items = new ArrayList<IDeformationItem>();
		rigidItems = new ArrayList<IRigidItem>();

		// first computes PatchIntersectionItem
		Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
		LineIntersection lineInter = new LineIntersection(bathy.getPolyline());

		LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
		if (null != I) {
			LinePairingItem item = new PatchIntersectionItem(selectedHorizon, I);
			items.add(item);
			
			/*item = new LinePairingItem( selectedFault.getInterval(),  faultTarget );
			items.add( item );*/
		}

		Patch selectedPatch = deformationCaller.getScene().getSelected();
		rigidItems.add(new TranslateItem(selectedPatch, Vector2D.substract(prev, start)));

		// then restore initial geometry
		translateTo(start);
	
		return super.canDeform();
	}
	

	

	@Override
	protected void computeTargets() {
		selectedPatchGraphic.clearLines();
		
		Paleobathymetry bathy = selectedHorizon.getPatchLibrary().getPaleobathymetry();
		LineIntersection lineInter = new LineIntersection( bathy.getPolyline() );
		
		LinePointPair I = lineInter.getFirstIntersection(selectedHorizon.getInterval());
		if ( null != I ) {
			selectedPatchGraphic.addLine( I.getPoint().getLine() );
			
			/*faultTarget = createDefaultTarget(I);
			selectedPatchGraphic.addLine( faultTarget );*/
		}
		
		selectedPatchGraphic.draw();
		
	}
	
	
	/**
	 * Create a vertical {@link IPolyline} from intersection point I to bottom
	 * of selectedComposite {@link Patch}
	 */
	private ExplicitPolyLine createDefaultTarget(LinePointPair I) {
		
		Scene scene = deformationCaller.getScene();
		Patch selected = scene.getSelected();
		double length = 1.5*selected.getBorder().getBoundingBox().height();
		List<Point2D> pts = new ArrayList<Point2D>();
		Point2D origin = I.getPoint().getPosition();
		Point2D target = new Point2D(origin);
		
		origin.setY( origin.y() + length );
		
		target.setY( target.y() - length );
		pts.add( origin );
		pts.add( target );
		
		ExplicitPolyLine faultTarget = new ExplicitPolyLine();
		faultTarget.initialize(pts);
		
		return faultTarget;
	}




	
	

}
