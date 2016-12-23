package stratifx.application.views;

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.extensions.ray.RayExtension;
import fr.ifp.kronosflow.polyline.LinePoint;
import stratifx.canvas.graphics.GSegment;

public class GExtension extends GSegment implements IUpdateGeometry {
		
		private double length;
	
		public GExtension( IExtension extension, double length ){
			setUserData(extension);
			this.length = length;
		}

		public IExtension getExtension(){
			return (IExtension)getUserData();
		}

		@Override
		public boolean canDeform() {
			return true;
		}

		@Override
		public void setDeformation(Deformation deformation) {
		// TODO Auto-generated method stub		
		}

		@Override
		public void updateGeometry(){

		
			IExtension extension = getExtension();
			
			if ( extension instanceof RayExtension ){
				
				RayExtension re = (RayExtension)extension;
				
				double[] dir = re.getDirection();
				LinePoint lp = re.getStartPoint();
				
				int npts =  2;
				double[] xpts = new double[npts];
				double[] ypts = new double[npts];
				
				double[] pos = new double[2];
				lp.getPosition(pos);
				
				xpts[0] = pos[0];
				ypts[0] = pos[1];

				xpts[1] = pos[0] + dir[0]*length;
				ypts[1] = pos[1] + dir[1]*length;
				
				setWorldGeometry(xpts, ypts);
			}
		
		
			
		

		}

}
