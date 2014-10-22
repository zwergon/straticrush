package straticrush.view;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import fr.ifp.kronosflow.controller.Event;
import fr.ifp.kronosflow.model.CtrlNode;
import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PolyLine;
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
	
	private static GColor getRandomPastelColor() {
		Random r = new Random();
		return GColor.getHSBColor(r.nextFloat(), (float) (0.1 + 0.2 * r.nextFloat()),
				(float) (0.3 + 0.5 * r.nextFloat()));
	}


	protected void createGSegments(Patch patch) {
		if ( null != patch.getBorder() ){
			GColor color = getRandomPastelColor();
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

	
	
	private class GPolyline extends GSegment {
		public GPolyline( PolyLine line ){
			setUserData(line);
		}
		
		public PolyLine getLine(){
			return (PolyLine)getUserData();
		}
		
	};

	
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
				
				GPolyline gline = (GPolyline)segment;
				PolyLine line = gline.getLine();
				
				List<CurviPoint> pts = line.getPoints();

				int npts =  ( line.isClosed()) ? pts.size()+1 : pts.size();
				double[] xpts = new double[npts];
				double[] ypts = new double[npts];

				double[] w_pt = new double[2];
				int i = 0;
				Iterator<ICurviPoint> itr = line.iterator();
				while( itr.hasNext() ){
					CurviPoint tp = (CurviPoint)itr.next();
					line.getPosition(tp, w_pt);
					xpts[i] = w_pt[0];
					ypts[i] = w_pt[1];
					i++;
				}	
				if ( line.isClosed() ){
					CurviPoint tp = (CurviPoint)pts.get(0);
					line.getPosition(tp, w_pt);
					xpts[npts-1] = w_pt[0];
					ypts[npts-1] = w_pt[1];
				}
				
				gline.setGeometry(xpts, ypts);
			}
		}
		
		
	}
	
	public Patch getObject(){
		return (Patch)getUserData();
	}
	
	@Override
	public void objectChanged(Object shape, Event event) {
		if ( shape == getObject() ){
			updateGeometry();
		}
	}
	
	

	

}
