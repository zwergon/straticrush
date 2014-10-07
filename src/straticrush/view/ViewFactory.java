package straticrush.view;

import java.util.HashMap;
import java.util.Map;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import no.geosoft.cc.graphics.GObject;

public class ViewFactory {
	
	private static Map<String, String> map_;
	
	static {
		map_ = new HashMap<String, String>();
		registerView( Patch.class, PatchView.class );
		registerView( MeshPatch.class, PatchView.class );
	}
	
	public static void registerView( Class<?> object_class, Class<?> view_class ){
		map_.put( object_class.getCanonicalName(), view_class.getCanonicalName() );
	}
	
	public static GObject createView( Object object ){

		GObject view = null;
	    try {
	    	/*
	    	 * TODO go through class inheritance to find the first ascending 
	    	 * class valid to create a View
	    	 */
	    	Class c1 = Class.forName( map_.get(object.getClass().getCanonicalName() ) );
	    	view = (GObject)c1.newInstance();
	    	if ( null != view ){
	    		view.setUserData( object );
	    	}
	    }
	    catch( Exception ex){
	    }
	    
	    return view;
	}

}
