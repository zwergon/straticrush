package straticrush.view;

import java.util.HashMap;
import java.util.Map;

import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
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
		registerView( ExplicitPatch.class, PatchView.class );
		registerView( CompositePatch.class, PatchView.class );
		registerView( Contact.class, ContactView.class );
		registerView( Paleobathymetry.class, PaleoView.class );
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
	
	public View createView( GScene scene, Object object ){

		if ( ( object == null ) || (scene == null ) ) {
			return null;
		}
		View view = null;
	    try {
	    	/*
	    	 * TODO go through class inheritance to find the first ascending 
	    	 * class valid to create a View
	    	 */
	    	String key = object.getClass().getCanonicalName();
	    	
	    	if ( !mapViews.containsKey(key) ){
	    		return null;
	    	}
	    	
	    	Class<?> c1 = Class.forName( mapViews.get(key) );
	    	if ( c1 == null ){
	    		return null;
	    	}
	    	view = (View)c1.newInstance();
	    	if ( null != view ){
	    		scene.add( view );
	    		view.setUserData( object );
	    	}
	    }
	    catch( Exception ex){
	    	System.out.println(ex.toString());
	    }
	    
	    return view;
	}
	
	public void destroyView( GScene scene, View view ){
		scene.remove(view);
		view.destroy();
	}
	
}
