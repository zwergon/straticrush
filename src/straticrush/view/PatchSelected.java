package straticrush.view;



import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.implicit.MeshPatch;



public class PatchSelected extends PatchView {
	
	protected void create_gsegments(Patch patch) {
		if ( null != patch.getBorder() ){
			addPolyLine( patch.getBorder(), new GColor(1.0f, 0.0f, 0.0f, 0.2f) );
		}
	}

}
