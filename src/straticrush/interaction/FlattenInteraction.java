package straticrush.interaction;

import java.awt.Color;

import straticrush.view.GInterval;
import straticrush.view.PatchView;
import fr.ifp.jdeform.deformation.FlattenController;
import fr.ifp.jdeform.deformation.NodeMoveController;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
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
	private GSegment  selectedSegment;
	private GObject    interaction_;
	private PatchInterval  selectedInterval;
	private int       x0_, y0_;
	FlattenController<Patch> flattenController = null;
	
	

	@SuppressWarnings("unchecked")
	public FlattenInteraction( GScene scene, String type ){
		scene_ = scene;
		selectedSegment = null;
		selectedInterval = null;
		flattenController = (FlattenController<Patch> )StratiCrushServices.getInstance().createController(type);
		
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
					PatchView view = (PatchView)gobject;
					selectedInterval = view.selectFeature(event.x, event.y);
					
					selectedSegment = new GInterval(selectedInterval);
					
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
					style.setLineWidth (1);
					selectedSegment.setStyle (style);
					
					interaction_.addSegment( selectedSegment );
				}
			}

			x0_ = event.x;
			y0_ = event.y;

			break;

		case GEvent.BUTTON1_DRAG :
			int dx = event.x - x0_;
			int dy = event.y - y0_;
			if ( null != selectedSegment ) {

				/*
				PatchView view = (PatchView)selectedSegment.getOwner();
				
				nodeMove.setObject(view.getObject()); 

				GTransformer transformer = view.getTransformer();

				int[] d_pos = transformer.worldToDevice(selectedInterval.getPosition());
				d_pos[0] += dx;
				d_pos[1] += dy;

				double[] w_pos = transformer.deviceToWorld(d_pos);

				nodeMove.setMove( selectedInterval, w_pos );
				flattenController.move();
				
				*/


			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			interaction_.removeSegment(selectedSegment);
			selectedInterval = null;
			selectedSegment = null;
			break;
			
		case GEvent.ABORT:
			flattenController.dispose();
			break;
		}
		
		scene_.refresh();

	}

}
