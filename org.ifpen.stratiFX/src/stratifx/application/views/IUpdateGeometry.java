package stratifx.application.views;

import fr.ifp.jdeform.deformation.Deformation;

public interface IUpdateGeometry {
	
	
	public boolean canDeform();
	public void setDeformation( Deformation deformation );
	public void updateGeometry();
}
