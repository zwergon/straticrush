package stratifx.application.views;

import fr.ifp.jdeform.deformation.Deformation;

public interface IDeformableGeometry {
	
	public boolean canDeform();
	public void setDeformation( Deformation deformation );
	
}
