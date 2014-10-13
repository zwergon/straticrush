package straticrush.view;

import java.util.HashMap;
import java.util.Map;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.topology.Contact;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;

public class ViewFactory {
	
	private Map<String, String> mapViews;
	private static ViewFactory factory;
	
	static {
		factory = null;
	}
	
	private ViewFactory(){
		mapViews = new HashMap<String, String>();
		registerView( Patch.class, PatchView.class );
		registerView( MeshPatch.class, PatchView.class );
		registerView( Contact.class, ContactView.class );
	}
	
	public static ViewFactory getInstance(){
		if (  null == factory ){
			factory = new ViewFactory();
		}
		return factory;
	}
	
	public void registerView( Class<?> object_class, Class<?> view_class ){
		mapViews.put( object_class.getCanonicalName(), view_class.getCanonicalName() );
	}
	
	public GObject createView( GScene scene, Object object ){

		GObject view = null;
	    try {
	    	/*
	    	 * TODO go through class inheritance to find the first ascending 
	    	 * class valid to create a View
	    	 */
	    	Class<?> c1 = Class.forName( mapViews.get(object.getClass().getCanonicalName() ) );
	    	view = (GObject)c1.newInstance();
	    	if ( null != view ){
	    		scene.add( view );
	    		view.setUserData( object );
	    	}
	    }
	    catch( Exception ex){
	    }
	    
	    return view;
	}
	
	public void destroyView( GScene scene, GObject view ){
		scene.remove(view);
		view.destroy();
	}
	
}
