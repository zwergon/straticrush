package stratifx.application.views;

import java.util.List;

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.CurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import stratifx.canvas.graphics.GSegment;

public class GInterval extends GSegment implements IUpdateGeometry {
	
	public Deformation deformation;
	
	public GInterval( PatchInterval line ){
		setUserData(line);
	}

	public PatchInterval getPatchInterval(){
		return (PatchInterval)getUserData();
	}
	
	@Override
	public boolean canDeform() {
		return false;
	}
	
	
	@Override
	public void setDeformation( Deformation deformation ){
		this.deformation = deformation;
	}

	@Override
	public void updateGeometry(){

	
		PatchInterval pinterval = getPatchInterval();
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
		
		setWorldGeometry(xpts, ypts);

	}

}
