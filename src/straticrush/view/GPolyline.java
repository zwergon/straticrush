package straticrush.view;

import java.util.Iterator;
import java.util.List;

import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.IPolyline;
import fr.ifp.kronosflow.model.PolyLine;
import no.geosoft.cc.graphics.GSegment;

public class GPolyline extends GSegment implements IUpdateGeometry {


	public GPolyline( IPolyline iPolyline ){
		setUserData(iPolyline);
	}

	public IPolyline getLine(){
		return (IPolyline)getUserData();
	}

	@Override
	public void updateGeometry(){

		IPolyline line = getLine();

		List<ICurviPoint> pts = line.getPoints();

		int npts =  ( line.isClosed()) ? pts.size()+1 : pts.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];

		double[] w_pt = new double[2];
		int i = 0;
		Iterator<ICurviPoint> itr = line.iterator();
		while( itr.hasNext() ){
			ICurviPoint tp = itr.next();
			line.getPosition(tp, w_pt);
			xpts[i] = w_pt[0];
			ypts[i] = w_pt[1];
			i++;
		}	
		if ( line.isClosed() ){
			ICurviPoint tp = pts.get(0);
			line.getPosition(tp, w_pt);
			xpts[npts-1] = w_pt[0];
			ypts[npts-1] = w_pt[1];
		}

		setGeometry(xpts, ypts);

	}

}
