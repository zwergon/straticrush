package straticrush.view;

import fr.ifp.jdeform.continuousdeformation.Deformation;

public interface IUpdateGeometry {
	
	
	public boolean canDeform();
	public void setDeformation( Deformation deformation );
	public void updateGeometry();
}
