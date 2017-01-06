package stratifx.application.views;

import fr.ifp.jdeform.deformation.Deformation;
import fr.ifp.kronosflow.warp.IWarp;
import stratifx.canvas.graphics.GObject;

public abstract class GDeformableObject  
	extends 
		GObject 
	implements 
		IDeformableGeometry 
{
	
	Deformation deformation = null;
	
	boolean enabledDeformation = true;
	
	
	public GDeformableObject( Object object ) {
		setUserData(object);
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
	protected void draw() {
		
		IWarp warp = (deformation != null ) ? deformation.getWarp() : null;	
		if ( enabledDeformation && ( null != warp ) ){	
			warpedDraw(warp);
		}
		else {
			directDraw();
		}
	}
	
	
	protected abstract void warpedDraw( IWarp warp );
	protected abstract void directDraw();

}
