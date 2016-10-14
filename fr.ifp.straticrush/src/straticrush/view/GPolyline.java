package straticrush.view;

import java.util.Iterator;
import java.util.List;

import no.geosoft.cc.graphics.GSegment;
import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.warp.IWarp;

public class GPolyline extends GSegment implements IUpdateGeometry {

	Deformation deformation = null;
	
	boolean enabledDeformation = true;

	public GPolyline( IPolyline iPolyline ){
		setUserData(iPolyline);
	}

	public IPolyline getLine(){
		return (IPolyline)getUserData();
	}
	
	@Override
	public boolean canDeform() {
		return enabledDeformation;
	}
	
	public void enableDeformation( boolean canDeform ){
		enabledDeformation = canDeform;
	}
	
	@Override
	public void setDeformation(Deformation deformation){
		this.deformation = deformation;
	}

	@Override
	public void updateGeometry(){
		
		IWarp warp = (deformation != null ) ? deformation.getWarp() : null;	
		if ( enabledDeformation && ( null != warp ) ){	
			drawWithDeformation(warp);
		}
		else {
			draw();
		}

	}


	private void draw() {
		IPolyline line = getLine();

		List<ICurviPoint> pts = line.getPoints();

		int npts =  ( line.isClosed()) ? pts.size()+1 : pts.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];
		
		double[] w_pt = new double[2];
		
		int i = 0;
		for( ICurviPoint tp : pts ){
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

	private void drawWithDeformation(IWarp warp) {
		IPolyline line = getLine();

		List<ICurviPoint> pts = line.getPoints();

		int npts =  ( line.isClosed()) ? pts.size()+1 : pts.size();
		double[] xpts = new double[npts];
		double[] ypts = new double[npts];
		
		double[] w_pt = new double[2];
		double[] w_dst = new double[2];
		int i = 0;
		Iterator<ICurviPoint> itr = line.iterator();
		while( itr.hasNext() ){
			ICurviPoint tp = itr.next();
			line.getPosition(tp, w_pt);
			warp.getDeformed(w_pt, w_dst);
			xpts[i] = w_dst[0];
			ypts[i] = w_dst[1];
			i++;
		}	
		if ( line.isClosed() ){
			ICurviPoint tp = pts.get(0);
			line.getPosition(tp, w_pt);
			warp.getDeformed(w_pt, w_dst);
			xpts[npts-1] = w_dst[0];
			ypts[npts-1] = w_dst[1];
		}
		
		setGeometry(xpts, ypts);
	}

}
