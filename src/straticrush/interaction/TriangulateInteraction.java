package straticrush.interaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import straticrush.view.GPolyline;
import straticrush.view.IUpdateGeometry;
import straticrush.view.PatchView;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.mesh.Triangle;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.IHandle;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.algo.ComputeBloc;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.sampling.CompactPointSampling;
import fr.ifp.kronosflow.model.sampling.PointSampling;
import fr.ifp.kronosflow.model.sampling.RandomPointSampling;
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
	
	Triangulation triangulation = null;
	
	
	private class PointSet extends GObject {
		private GSegment[] lines_;
		static final int nPoints = 200; 
		RectD bbox;
		
		public PointSet ()
		{
			
			
			setVisibility( DATA_VISIBLE | SYMBOLS_VISIBLE );
						
			// Add style to object itself so it is inherited by segments
			GStyle lineStyle = new GStyle();
			setStyle (lineStyle);

		
		}
		
		public void setPoints( PolyLine border ){
			this.bbox = border.getBoundingBox();
			
			removeSegments();
			triangulation = new Triangulation();
			
			double[] xy = new double[2];
			for( ICurviPoint cp : border.getPoints() ){
				border.getPosition(cp, xy );
				Node node = new Node(xy);
				node.setFixed(true);
				triangulation.addNode(node);
			}
			
			PointSampling sampling = new CompactPointSampling(bbox);
			sampling.sample( nPoints ); 

			
			PolyLineGeometry geom = new PolyLineGeometry(border);

			for( Point2D pt : sampling.getPoints() ){				
				if ( geom.isPointInside( pt.getPosition() )){
					Node node = new Node( pt.getPosition()  );
					triangulation.addNode( node );
				}
			}
			
			double dmax = ( bbox.width() > bbox.height() ) ? bbox.width() : bbox.height();
			triangulation.setThresholdDistance( dmax / 100. );

			triangulation.execute();
		
			//remove triangles outside of border.
			List<Triangle> toRemove = new ArrayList<Triangle>();
			for( IHandle handle : triangulation.getCells() ){
				Triangle trgl = (Triangle)handle;
				double[] bary = trgl.barycenter( triangulation );
				if ( !geom.isPointInside(bary) ){
					toRemove.add(trgl);
				}
			}
			for( Triangle trgl : toRemove ){
				triangulation.removeCell(trgl);
			}
			
			Collection<IHandle> triangles = triangulation.getCells();
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
			if ( null == triangulation ){
				return;
			}
			double[] xy = new double[8];
			int i = 0;
			for ( IHandle handle : triangulation.getCells() ){
				Triangle trgl = (Triangle)handle;
				
				Node n1 = (Node) triangulation.getNode(trgl.getNodeId(0));
				xy[0] = n1.x();
				xy[1] = n1.y();
				
				Node n2 = (Node) triangulation.getNode(trgl.getNodeId(1));
				xy[2] = n2.x();
				xy[3] = n2.y();

				Node n3 = (Node) triangulation.getNode(trgl.getNodeId(2));
				xy[4] = n3.x();
				xy[5] = n3.y();

				xy[6] = n1.x();
				xy[7] = n1.y();
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
