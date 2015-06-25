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

public class AutoTargetsManipulator implements IStratiManipulator {
	
	


	protected GScene scene;
	
	protected GPatchObject interaction;
	
	protected PatchInterval  selectedHorizon = null;
	
	protected PatchInterval  selectedFault = null;
	
	protected Patch selectedComposite;
	
	protected List<Patch> surroundedComposites;
	
	List<LinePairingItem> items =  new ArrayList<LinePairingItem>();
	
	
	public AutoTargetsManipulator( GScene scene, Patch selectedComposite, List<Patch> surroundedComposites ){
		this.scene = scene;
		this.selectedComposite = selectedComposite;
		this.surroundedComposites = surroundedComposites;
	}
	
	@Override
	public boolean isActive(){
		return (interaction != null );
	}
	

	@Override
	public void activate() {
		if ( null != selectedComposite ){
			interaction = new GPatchObject();
			scene.add(interaction);
			for( Patch p : surroundedComposites ){
				interaction.addOutline( p, true );
			}
			interaction.addOutline( selectedComposite, false );
		}	
		StratiCrushServices.getInstance().addListener(interaction);
	}

	@Override
	public void deactivate() {
		StratiCrushServices.getInstance().removeListener(interaction);
		scene.remove(interaction);
		interaction.removeSegments();;
		interaction.remove();
		interaction = null;
	}

	@Override
	public GObject getInteraction() {
		return interaction;
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
	
	public List<LinePairingItem> getItems() {
		return items;
	}
	
	
	/**
	 * retrieves the {@link PatchInterval} of type c that is nearest of ori.
	 * @see findHorizonFeature
	 * @see findFaultFeature 
	 */
	protected <T> PatchInterval findFeature( double[] ori, Class<T> c ) {
		
		PatchInterval interval = null;
		double minDist = Double.POSITIVE_INFINITY;
		
		for( KinObject object : selectedComposite.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				Interval fgInterval = ((FeatureGeolInterval)object).getInterval();
				if ( c.isInstance(fgInterval.getFeature()) ){
					PolyLineGeometry pgeom = new PolyLineGeometry(fgInterval);
					
					double dist = pgeom.minimalDistance( ori );
					if ( dist < minDist ){
						interval = (PatchInterval)object;
						minDist = dist;
					}
				}
			}
		}
		
		return interval;
	}
	
	protected PatchInterval findHorizonFeature( double[] ori ){
		return findFeature( ori, StratigraphicEvent.class );
	}
	
	protected PatchInterval findFaultFeature( double[] ori ){
		return findFeature( ori, FaultFeature.class );
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
