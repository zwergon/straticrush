package straticrush.view;

import java.util.Iterator;
import java.util.List;

import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PolyLine;
import no.geosoft.cc.graphics.GSegment;

public class GInterval extends GSegment implements IUpdateGeometry {
	
	public GInterval( PatchInterval line ){
		setUserData(line);
	}

	public PatchInterval getPatchInterval(){
		return (PatchInterval)getUserData();
	}

	@Override
	public void updateGeometry(){

	
		PatchInterval pinterval = getPatchInterval();
		Interval interval = pinterval.getInterval();
	
		int npts =  interval.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		double[] w_pt = new double[2];
		int i = 0;
		Iterator<ICurviPoint> itr = interval.iterator();
		while( itr.hasNext() ){
			CurviPoint tp = (CurviPoint)itr.next();
			interval.getPosition(tp, w_pt);
			xpts[i] = w_pt[0];
			ypts[i] = w_pt[1];
			i++;
		}	
		
		setGeometry(xpts, ypts);

	}

}
