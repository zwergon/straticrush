package straticrush.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import straticrush.view.GPolyline;
import straticrush.view.IUpdateGeometry;
import straticrush.view.PatchView;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.algo.ComputeBloc;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.triangulation.Triangle;
import fr.ifp.kronosflow.triangulation.Triangulation;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;

public class TriangulateInteraction implements GInteraction {
	
	private GScene    scene_;
	private GInteraction    interaction_;
	private Patch composite;
	
	boolean first = true;
	
	
	private class PointSet extends GObject {
		private GSegment[] lines_;
		static final int nPoints = 800; 
		RectD bbox;
		
		double[] random;
		Set<Triangle> triangles;

		public PointSet ()
		{
			Random rg = new Random();
			
			random = new double[2*nPoints];
			for( int i=0; i<2*nPoints;i++ ){
				random[i] =  rg.nextDouble();
			}
			
			setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
						
			// Add style to object itself so it is inherited by segments
			GStyle lineStyle = new GStyle();
			setStyle (lineStyle);

		
		}
		
		public void setPoints( PolyLine border ){
			this.bbox = border.getBoundingBox();
			
			removeSegments();
			Triangulation triangulation = new Triangulation();
			
			double[] xy = new double[2];
			for( ICurviPoint cp : border.getPoints() ){
				border.getPosition(cp, xy );
				triangulation.addPoint(xy[0], xy[1], true );
			}

			PolyLineGeometry geom = new PolyLineGeometry(border);

			for (int i = 0; i < nPoints; i++) {
				xy[0] =  bbox.left + Math.round (bbox.width()  *random[2*i]);
				xy[1] =  bbox.top  + Math.round (bbox.height() *random[2*i+1]);
				
				if ( geom.isPointInside(xy ))
					triangulation.addPoint( xy[0], xy[1] );
			}
			
			double dmax = ( bbox.width() > bbox.height() ) ? bbox.width() : bbox.height();
			triangulation.setThresholdDistance( dmax / 100. );

			triangulation.execute();
			triangles = triangulation.getTriangles();
			
			//remove triangles outside of border.
			List<Triangle> toRemove = new ArrayList<Triangle>();
			for( Triangle trgl : triangles ){
				double[] bary = trgl.barycenter();
				if ( !geom.isPointInside(bary) ){
					toRemove.add(trgl);
				}
			}
			triangles.removeAll(toRemove);
			
			System.out.println("n triangles " + triangles.size() );
			
			lines_ = new GSegment[triangles.size()];
			for (int i = 0; i < triangles.size(); i++) {
				lines_[i] = new GSegment();
				addSegment (lines_[i]);

				GStyle symbolStyle = new GStyle();
				symbolStyle.setForegroundColor (new GColor (0, 0, 255));
				symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
				GImage square = new GImage (GImage.SYMBOL_SQUARE1);
				square.setStyle (symbolStyle);

				lines_[i].setVertexImage (square);
			}
		}



		public void draw()
		{
			double[] xy = new double[8];
			int i = 0;
			for ( Triangle trgl : triangles ){
				xy[0] = trgl.get(0).x();
				xy[1] = trgl.get(0).y();
				xy[2] = trgl.get(1).x();
				xy[3] = trgl.get(1).y();
				xy[4] = trgl.get(2).x();
				xy[5] = trgl.get(2).y();
				xy[6] = trgl.get(0).x();
				xy[7] = trgl.get(0).y();
				lines_[i++].setGeometryXy(xy);			}
		}
			
		
	}


	
	private class GInteraction extends GObject {
		
		PointSet pointSet;
		
		public GInteraction(){
			super("Interaction");
			setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
			
			pointSet = new PointSet();
			add( pointSet );
		}
		
		
		
		void addOutline( Patch patch ){

			GPolyline borderLine = new GPolyline( patch.getBorder() );
			addSegment( borderLine );
			
			GStyle style = new GStyle();
			style.setBackgroundColor(GColor.CYAN);
			//style.setLineWidth (4);
			borderLine.setStyle (style);
			
			borderLine.updateGeometry();
			
			pointSet.setPoints(patch.getBorder());
		}
		
		@Override
		public void draw() {
			for( GSegment gsegment : getSegments() ){
				if ( gsegment instanceof IUpdateGeometry ){
					((IUpdateGeometry) gsegment).updateGeometry();
				}
			}
			
			for( GObject object: getChildren() ){
				object.draw();
			}
		}
		
	}
	
	
	public TriangulateInteraction( GScene scene, String type ){
		scene_ = scene;
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
								
					scene_.add(interaction_);
					interaction_.addOutline(composite);
					
					interaction_.draw();
					
					scene.refresh();
				}
			}
			break;

		case GEvent.BUTTON1_DRAG :
			break;

		case GEvent.BUTTON1_UP :
			
			if ( null != composite ){
				
				composite.remove();
				interaction_.removeSegments();;
				interaction_.remove();

				composite = null;
			
			}
			break;
			
		case GEvent.ABORT:
			break;
		}
		
		scene_.refresh();

	}

}
