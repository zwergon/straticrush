package straticrush.view;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.geosoft.cc.graphics.GColorMap;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;
import straticrush.interaction.StratiCrushServices;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.file.FileMeshPatch;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.style.PropertyStyle;
import fr.ifp.kronosflow.model.triangulation.TrglPatch;
import fr.ifp.kronosflow.property.IPropertyValue;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyStatistic;
import fr.ifp.kronosflow.topology.Border;
import fr.ifp.kronosflow.topology.Contact;
import fr.ifp.kronosflow.uids.UID;

public class Plot extends GScene {

	private Insets   insets_;
	
	GColorMap colormap;
	
	Property currentProp;
	
	static private Map<String, String> mapViews;
	
	static {
		mapViews = new HashMap<String, String>();
		registerView( Patch.class, PatchView.class );
		registerView( MeshPatch.class, MeshPatchView.class );
		registerView( ExplicitPatch.class, PatchView.class );
		registerView( CompositePatch.class, PatchView.class );
		registerView( FileMeshPatch.class, MeshPatchView.class );
		registerView( TrglPatch.class, MeshPatchView.class );
		registerView( PatchInterval.class, PatchIntervalView.class );
		registerView( FeatureGeolInterval.class, PatchIntervalView.class );
		registerView( Contact.class, PartitionLineView.class );
		registerView( Border.class, PartitionLineView.class );
		registerView( Paleobathymetry.class, PaleoView.class );
	}
	
	static public void registerView( Class<?> object_class, Class<?> view_class ){
		mapViews.put( object_class.getCanonicalName(), view_class.getCanonicalName() );
	}
	
	public Plot (GWindow window, Insets insets)
	{
		super (window);
		insets_ = insets;
		
		colormap = new GColorMap();
		
		colormap.createDefaultColormap();
	}
	
	public StratiWindow getStratiWindow(){
		return (StratiWindow)getWindow();
	}

	public Property getCurrentProp() {
		
		Section section = StratiCrushServices.getInstance().getSection();

		Property property = null;
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		UID currentPropUID = propStyle.getCurrent();
		if ( currentPropUID != null ){
			property = section.getPropertyDB().findByUID(currentPropUID);

			if ( null != property ){
				updateColormap(property);
			}
		}

		return property;
	}
	
	public GColorMap getColorMap() {
		return colormap;
	}
	
	public View createView( Object object ){

		if (  object == null  ) {
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
	    		add( view );
	    		view.setModel( object );
	    	}
	    }
	    catch( Exception ex){
	    	System.out.println(ex.toString());
	    }
	    
	    return view;
	}
	
	public Collection<View> getViews(){
		Collection<View> views = new ArrayList<View>();
		for( GObject object :  getChildren()){
			if ( object instanceof View ){
				views.add((View)object);
			}
		}
		
		return views;
	}
	
	public void destroyViews( Object object ){
		
		Collection<Object> objects = new ArrayList<Object>();
		objects.add(object);
		if ( object instanceof KinObject ){
			KinObject kobject =(KinObject)object;
			collectChildren( kobject, objects );
		}
		
		for( View view : getViews() ){
			for( Object o : objects ){
				if ( o == view.getUserData() ){
					remove(view);
				}
			}
		}
	}
	

	public void destroyView( View view ){	
		remove(view);
	}
	
	public void destroyAllViews(){
		for( View view :  getViews()){
			destroyView(view);
		}
	}
	
	/**
	 * Notify all listeners about change in this Shape.
	 * 
	 * @param Event  Describe the change in the Shape.
	 */
	public void notifyViews( IControllerEvent<?> event )
	{
		for( GObject object : getChildren() ){
			if ( object instanceof View ){
				View view = (View)object;
				view.modelChanged(event);
			}
		}
	}
	
	protected void resize (double dx, double dy)
	{
		super.resize (dx, dy);
		setViewport (insets_.left, insets_.top,
				getWindow().getWidth() - insets_.left - insets_.right,
				getWindow().getHeight() - insets_.top - insets_.bottom);
	}

	private void updateColormap(Property property ) {

		PropertyStatistic stat = property.getAccessor().getStatistic();

		if ( stat == null ){
			return;
		}


		IPropertyValue min = stat.getMinValue();
		IPropertyValue max = stat.getMaxValue();

		colormap.setMinMax( min.real(), max.real() );
	}
	
	/**
	 * Method that goes though {@link KinObject} tree and collect list of all children of
	 * this root {@link KinObject}.
	 * @param kobject the root {@link KinObject}
	 * @param objects flat list of children
	 */
	private void collectChildren(KinObject kobject, Collection<Object> objects) {
		List<KinObject> children = kobject.getChildren();
		if ( children.isEmpty() ){
			return;
		}
		objects.addAll(children);
		for( KinObject child : children ){
			collectChildren(child, objects);
		}
	}




}


