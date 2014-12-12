package straticrush.interaction;

import java.awt.Color;

import straticrush.view.GInterval;
import straticrush.view.PatchView;
import fr.ifp.jdeform.continuousdeformation.FlattenConstraint;
import fr.ifp.jdeform.deformation.FlattenController;
import fr.ifp.jdeform.deformation.NodeMoveController;
import fr.ifp.jdeform.deformation.TranslateNodeMove;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.LinePoint;
import fr.ifp.kronosflow.model.LinePointPair;
import fr.ifp.kronosflow.model.Paleobathymetry;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.algo.LineIntersection;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GTransformer;

public class FlattenInteraction implements GInteraction {
	
	private GScene    scene_;
	private GInterval  selectedSegment;
	private GObject    interaction_;
	private PatchInterval  selectedInterval;
	private int       x0_, y0_;
	FlattenController<Patch> flattenController = null;
	PatchView view = null;
	
	LineIntersection lineInter = null;
	
	TranslateNodeMove translateController = null;
	
	

	@SuppressWarnings("unchecked")
	public FlattenInteraction( GScene scene, String type ){
		scene_ = scene;
		selectedSegment = null;
		selectedInterval = null;
		flattenController = (FlattenController<Patch> )StratiCrushServices.getInstance().createController(type);
		translateController = (TranslateNodeMove)StratiCrushServices.getInstance().createController("Translate");
		
		 // Create a graphic node for holding the interaction graphics
	    interaction_ = new GObject ("Interaction");
	    
	    
	  
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
					
					
					view = (PatchView)gobject;
					selectedInterval = view.selectFeature(event.x, event.y);
					
					Paleobathymetry bathy = selectedInterval.getPatchLibrary().getPaleobathymetry();
					
					lineInter = new LineIntersection( bathy.getPaleoLine() );
					scene_.add(interaction_);
					
					selectedSegment = new GInterval(selectedInterval);
					interaction_.addSegment( selectedSegment );
					
					BoundaryFeature feature = selectedInterval.getInterval().getFeature();
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
					
					
					((GInterval)selectedSegment).updateGeometry();
					
					scene.refresh();
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GEvent.BUTTON1_DRAG :
			
			if ( null != selectedSegment ) {
				
				LinePointPair I = lineInter.getFirstIntersection(selectedInterval.getInterval());
				if ( null != I ) {
					
					flattenController.setPatch(view.getObject()); 
					
					Paleobathymetry bathy = selectedInterval.getPatchLibrary().getPaleobathymetry();
					flattenController.setFlattenConstraint( new FlattenConstraint(selectedInterval, bathy) );
					flattenController.setPointConstraint( I );
					flattenController.move();
					
				}
				else {
				
					translateController.setPatch(view.getObject()); 

					GTransformer transformer = view.getTransformer();

					int[] oldPos = new int[2];
					oldPos[0] = x0_;  oldPos[1] = y0_;
					int[] newPos = new int[2];
					newPos[0] = event.x;  newPos[1] = event.y;
					double[] d_pos1 = transformer.deviceToWorld(oldPos);
					double[] d_pos2 = transformer.deviceToWorld(newPos);
					

					translateController.setTranslation(Vector2D.substract(d_pos2, d_pos1));
					translateController.move();
					
					
					
				}
				selectedSegment.updateGeometry();
				scene.refresh();

			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			interaction_.removeSegment(selectedSegment);
			interaction_.remove();
			selectedInterval = null;
			selectedSegment = null;
			break;
			
		case GEvent.ABORT:
			/*flattenController.dispose();
			scene_.remove(interaction_);*/
			break;
		}
		
		scene_.refresh();

	}

}
