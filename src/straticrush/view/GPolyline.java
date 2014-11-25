package straticrush.view;

import java.util.Iterator;
import java.util.List;

import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.interfaces.ICurviPoint;
import no.geosoft.cc.graphics.GSegment;

public class GPolyline extends GSegment {


	public GPolyline( PolyLine line ){
		setUserData(line);
	}

	public PolyLine getLine(){
		return (PolyLine)getUserData();
	}

	public void updateGeometry(){

		PolyLine line = getLine();

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

		setGeometry(xpts, ypts);

	}

}
