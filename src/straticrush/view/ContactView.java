package straticrush.view;


import java.util.Iterator;
import java.util.List;

import straticrush.interaction.IViewListener;
import straticrush.interaction.StratiCrushServices;
import fr.ifp.kronosflow.controller.Event;
import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.interfaces.ICurviPoint;
import fr.ifp.kronosflow.topology.Contact;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;

public class ContactView extends View {
	
	private GSegment gline;
	

	public void setUserData (Object object)
	{
		super.setUserData( object );
		
		Contact contact = (Contact)getUserData();

		PatchInterval interval = contact.getInterval();
			
		gline = new GSegment();
		gline.setUserData( interval );
		addSegment(gline);

		updateGeometry();
		
		GStyle style = new GStyle();
		style.setForegroundColor ( new GColor( (int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255 )));
		style.setLineWidth (3);
		gline.setStyle (style);	
	}
	
	private Patch getPatch(){
		Contact contact = (Contact)getUserData();
		PatchInterval interval = contact.getInterval();
		return interval.getPatch();
	}
	
	
	@Override
	public void draw() {
		updateGeometry();
	}

	private void updateGeometry() {
		PatchInterval pinterval = (PatchInterval)gline.getUserData();
		Interval interval = pinterval.getInterval();
		List<ICurviPoint> pts = interval.getPoints();
		PolyLine line = interval.getPolyline();

		int npts =  pts.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		double[] w_pt = new double[2];
		int i = 0;
		Iterator<ICurviPoint> itr = pts.iterator();
		while( itr.hasNext() ){
			CurviPoint tp = (CurviPoint)itr.next();
			line.getPosition(tp, w_pt);
			xpts[i] = w_pt[0];
			ypts[i] = w_pt[1];
			i++;
		}	
		
		gline.setGeometry(xpts, ypts);
	}
	
	@Override
	public void objectChanged(Object object, Event event) {
		if ( object == getPatch() ){
			updateGeometry();
		}
	}

}
