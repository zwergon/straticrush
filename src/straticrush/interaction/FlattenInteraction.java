package straticrush.interaction;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import straticrush.view.GInterval;
import straticrush.view.GPolyline;
import straticrush.view.IUpdateGeometry;
import straticrush.view.PatchView;
import straticrush.view.Plot;
import straticrush.view.View;
import straticrush.view.ViewFactory;
import fr.ifp.jdeform.continuousdeformation.FlattenConstraint;
import fr.ifp.jdeform.deformation.FlattenController;
import fr.ifp.jdeform.deformation.NodeMoveController;
import fr.ifp.jdeform.deformation.TranslateNodeMove;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.LinePoint;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.algo.ComputeBloc;
import fr.ifp.kronosflow.model.algo.LineIntersection;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GTransformer;

public class FlattenInteraction implements GInteraction {
	
	private GScene    scene_;
	private GInteraction    interaction_;
	private PatchInterval  selectedInterval;
	private Patch composite;
	private int       x0_, y0_;
	FlattenController<Patch> flattenController = null;
	boolean first = true;

	LineIntersection lineInter = null;
	
	TranslateNodeMove translateController = null;
	
	private class GInteraction extends GObject {
		public GInteraction(){
			super("Interaction");
			setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
		}
		
		void addInterval( PatchInterval interval ){
			
			GInterval selectedSegment = new GInterval(interval);
			addSegment( selectedSegment );
			
			BoundaryFeature feature = interval.getInterval().getFeature();
			GColor color = null;
			if ( null != feature ){
				Color bcolor = feature.getColor();
				color = new GColor( bcolor.getRed(), bcolor.getGreen(), bcolor.getBlue() );
			}
			else {
				color = GColor.CYAN;
			}

			GStyle style = new GStyle();
			style.setForegroundColor (color);
			style.setLineWidth (4);
			selectedSegment.setStyle (style);


			selectedSegment.updateGeometry();
			
		}
		
		
		void addOutline( Patch patch ){
			
			GPolyline borderLine = new GPolyline(patch.getBorder());
			addSegment( borderLine );
			
			GStyle style = new GStyle();
			style.setBackgroundColor(GColor.CYAN);
			//style.setLineWidth (4);
			borderLine.setStyle (style);
			
			borderLine.updateGeometry();
		}
		
		@Override
		public void draw() {
			for( GSegment gsegment : getSegments() ){
				if ( gsegment instanceof IUpdateGeometry ){
					((IUpdateGeometry) gsegment).updateGeometry();
				}
			}
		}
		
		public void clearLines(){
			for ( GPolyline line : lines ){
				removeSegment(line);
			}
			lines.clear();
		}

		public void addLine(PolyLine targetLine) {
			
			GPolyline gline  = new GPolyline(targetLine);
			addSegment( gline );
			
			GStyle style = new GStyle();
			style.setForegroundColor(GColor.BLUE);
			style.setLineWidth (2);
			gline.setStyle (style);
			
			
			GStyle symbolStyle = new GStyle();
			symbolStyle.setForegroundColor (new GColor (0, 0, 255));
			symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
			GImage square = new GImage (GImage.SYMBOL_SQUARE1);
			square.setStyle (symbolStyle);

			gline.setVertexImage (square);
			
			gline.updateGeometry();
		
			
			lines.add( gline );
			
		}
		
		List<GPolyline> lines = new ArrayList<GPolyline>();
	
	}
	
	

	@SuppressWarnings("unchecked")
	public FlattenInteraction( GScene scene, String type ){
		scene_ = scene;
		selectedInterval = null;
		flattenController = (FlattenController<Patch> )StratiCrushServices.getInstance().createController(type);
		translateController = (TranslateNodeMove)StratiCrushServices.getInstance().createController("Translate");
		
		 // Create a graphic node for holding the interaction graphics
	    interaction_ = new GInteraction();
	    
	}
	

	@Override
	public void event(GScene scene, GEvent event) {
		if ( scene != scene_ ){
			return;
		}

		switch (event.type) {
		case GEvent.BUTTON1_DOWN :
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
				
					Patch patch = ((PatchView)gobject).getObject();
				
					ComputeBloc computeBloc = new ComputeBloc(patch.getPatchLibrary());
					composite = computeBloc.getBloc( patch );
									
					selectedInterval = findFeature( scene.getTransformer().deviceToWorld(event.x, event.y) );
				
					if ( null != selectedInterval ){
						Paleobathymetry bathy = selectedInterval.getPatchLibrary().getPaleobathymetry();
						lineInter = new LineIntersection( bathy.getPaleoLine() );

						scene_.add(interaction_);
						interaction_.addOutline( composite );
						interaction_.addInterval(selectedInterval);
					}					
					
					scene.refresh();
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GEvent.BUTTON1_DRAG :
			
			if ( null != selectedInterval  && first ) {
				
				LinePointPair I = lineInter.getFirstIntersection(selectedInterval.getInterval());
				if ( null != I ) {
					
					flattenController.setPatch(composite); 
					
					Paleobathymetry bathy = selectedInterval.getPatchLibrary().getPaleobathymetry();
					flattenController.setFlattenConstraint( new FlattenConstraint(selectedInterval, bathy) );
					flattenController.setPointConstraint( I );
					flattenController.move();
					
					interaction_.clearLines();
					interaction_.addLine( flattenController.getTargetLine() );
					interaction_.addLine( flattenController.getDebugLine() );
					first = false;
					
				}
				else {
				
					translateController.setPatch(composite); 

					GTransformer transformer = scene.getTransformer();

					int[] oldPos = new int[2];
					oldPos[0] = x0_;  oldPos[1] = y0_;
					int[] newPos = new int[2];
					newPos[0] = event.x;  newPos[1] = event.y;
					double[] d_pos1 = transformer.deviceToWorld(oldPos);
					double[] d_pos2 = transformer.deviceToWorld(newPos);
					

					translateController.setTranslation( Vector2D.substract(d_pos2, d_pos1) );
					translateController.move();
					
				}
				interaction_.draw();;
				scene.refresh();

			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			
			if ( null != selectedInterval ){
				
				composite.remove();
				interaction_.removeSegments();;
				interaction_.remove();

				composite = null;
				selectedInterval = null;
				first = true;
			}
			break;
			
		case GEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
		}
		
		scene_.refresh();

	}


	private PatchInterval findFeature( double[] ori ) {
		
		PatchInterval interval = null;
		double minDist = Double.POSITIVE_INFINITY;
		
		for( KinObject object : composite.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				Interval fgInterval = ((FeatureGeolInterval)object).getInterval();
				BoundaryFeature bf = (BoundaryFeature)fgInterval.getFeature();
				if ( bf instanceof StratigraphicEvent ){
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

}
