package stratifx.application.views;

import java.awt.Color;
import java.util.List;

import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.CurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLineGeometry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

public class GInterval extends GObject {
	
	
	public GInterval( PatchInterval interval ){
		
		setUserData(interval);
		
		GSegment gIntervalSegment = new GSegment();
		gIntervalSegment.setUserData( interval );
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
		gIntervalSegment.setStyle (style);
		
		
		addSegment( gIntervalSegment );

			
		FeatureInterval inter = interval.getInterval();
		PolyLineGeometry geom = new PolyLineGeometry( inter );
		
		for( int i = IExtension.BEFORE; i< IExtension.LAST; i++ ){
			IExtension extension = inter.getExtension(i);
			GExtension gextension =  new GExtension( extension, geom.length()/5. );
			addSegment( gextension );
			
			gextension.setStyle( style );
		}
	}
	

	public PatchInterval getPatchInterval(){
		return (PatchInterval)getUserData();
	}
	
	@Override
	public void draw(){
		
		for( GSegment segment : getSegments() ){
			Object userData = segment.getUserData();
			if ( userData instanceof PatchInterval ){
				drawInterval( segment );
			}
			else if ( userData instanceof IExtension ){
				((GExtension)segment).draw();
			}
			
		}

	}


	private void drawInterval(GSegment segment) {
		
		PatchInterval pinterval = (PatchInterval)segment.getUserData();
		FeatureInterval interval = pinterval.getInterval();
	
		List<ICurviPoint> pts = interval.getPoints();
		int npts =  pts.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		double[] w_pt = new double[2];
		int i = 0;
	
		for( ICurviPoint ip : pts ){
			CurviPoint tp = (CurviPoint)ip;
			interval.getPosition(tp, w_pt);
			xpts[i] = w_pt[0];
			ypts[i] = w_pt[1];
			i++;
		}	
		
		segment.setWorldGeometry(xpts, ypts);
		
	}



}
