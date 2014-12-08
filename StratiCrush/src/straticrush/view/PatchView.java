package straticrush.view;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import fr.ifp.kronosflow.controller.interfaces.IControllerEvent;
import fr.ifp.kronosflow.geology.BodyFeature;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.interfaces.ICurviPoint;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;


public class PatchView extends View {
	
	GSegment border = null;
	
	public PatchView(){
		
	}
	
	@Override
	public void setUserData (Object object)
	{
		super.setUserData( object );
	
		Patch patch = (MeshPatch)object;
		
		createGSegments(patch);
		
	}
	
	public GSegment getBorder(){
		return border;
	}
	
	public CtrlNode selectNode( int dev_x, int dev_y  ){
		
		double[] pos = getTransformer().deviceToWorld(dev_x, dev_y);
		Patch patch = getObject();
		double distance = Double.MAX_VALUE;
		CtrlNode nearest_node = null;
		for( CtrlNode ctl_node : patch.getNodes() ){
			
			double cur_distance = ctl_node.distance(pos);
			if ( cur_distance < distance ){
				distance = cur_distance;
				nearest_node = ctl_node;
			}
		}		
		return nearest_node;
	}
	
	public PatchInterval selectFeature( int dev_x, int dev_y  ){
		
		PatchInterval interval = null;
		Point2D ori = new Point2D( getTransformer().deviceToWorld(dev_x, dev_y) );
		Point2D dst = new Point2D();
		
		double minDist = Double.POSITIVE_INFINITY;
		
		Patch patch = (Patch)getObject();
		for( KinObject object : patch.getChildren() ){
			if ( object instanceof FeatureGeolInterval ){
				Interval fgInterval = ((FeatureGeolInterval)object).getInterval();
				BoundaryFeature bf = (BoundaryFeature)fgInterval.getFeature();
				if ( bf instanceof StratigraphicEvent ){
					PolyLineGeometry pgeom = new PolyLineGeometry(fgInterval);
					
					double dist = pgeom.minimalDistance( ori, dst );
					if ( dist < minDist ){
						interval = (PatchInterval)object;
						minDist = dist;
					}
				}
			}
		}
		
		
		return interval;
	}
	
	public GColor getPatchColor() {

		Patch patch = (Patch)getObject();

		GColor patchColor = null;
		BodyFeature feature = patch.getGeologicFeaturesByClass(StratigraphicUnit.class);

		if (feature != null) {
			Color acolor = feature.getColor();
			patchColor = new GColor(acolor.getRed(), acolor.getGreen(), acolor.getBlue(), acolor.getAlpha() );
		}
		else {
			patchColor = new GColor(255, 255, 255);
		}

		return patchColor;
	}
	
	private static GColor getRandomPastelColor() {
		Random r = new Random();
		return GColor.getHSBColor(r.nextFloat(), (float) (0.1 + 0.2 * r.nextFloat()),
				(float) (0.3 + 0.5 * r.nextFloat()));
	}


	protected void createGSegments(Patch patch) {
		if ( null != patch.getBorder() ){
			GColor color = getPatchColor();
			border = addPolyLine( patch.getBorder(), color );
			
			GStyle textStyle = new GStyle();
			textStyle.setForegroundColor (new GColor (100, 100, 150));
			textStyle.setBackgroundColor (null);
			int offset = 0;
			
			Iterator<ICurviPoint> itr = patch.getBorder().iterator();
			while( itr.hasNext() ){
				itr.next();
				GText text = new GText (String.valueOf(offset), GPosition.FIRST | GPosition.STATIC );
				text.setPositionOffset( offset++ );
				
				text.setStyle (textStyle);
				border.addText(text);
			}
			
			GStyle symbolStyle = new GStyle();
			symbolStyle.setForegroundColor (new GColor (0, 0, 255));
			symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
			GImage square = new GImage (GImage.SYMBOL_SQUARE1);
			square.setStyle (symbolStyle);

			border.setVertexImage (square);
		}
		
		/*for( PolyLine line : patch.getLines() ){
			addPolyLine( line, GColor.green );
		}*/
		
		setVisibility( GObject.DATA_VISIBLE | GObject.ANNOTATION_INVISIBLE | GObject.SYMBOLS_INVISIBLE );
	}

	


	
	protected GSegment addPolyLine( PolyLine line, GColor color ) {
		
		GSegment gline = new GPolyline( line );
		addSegment(gline);
		
			
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.black );
		style.setBackgroundColor(  color );
		style.setLineWidth (1);
		gline.setStyle (style);
		
		return gline;
	}
	
	
	public void draw()
	{
		updateGeometry();
	}


	public void updateGeometry() {
		
		if ( border == null ){
			return;
		}
		
		for( Object segment : getSegments() ){
			 if ( segment instanceof GPolyline ){
				 ((GPolyline)segment).updateGeometry();
			}
		}
		
		
	}
	
	public Patch getObject(){
		return (Patch)getUserData();
	}
	
	@Override
	public void objectChanged( IControllerEvent<?> event ) {
		if ( event.getObject() == getObject() ){
			updateGeometry();
		}
	}
	
	

	

}
