package straticrush.menu;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import straticrush.view.Plot;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GTransformer;
import no.geosoft.cc.graphics.GViewport;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.graphics.GWorldExtent;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.IColorProvider;
import fr.ifp.kronosflow.model.KinObject;

public class Menu extends GScene implements IMenuItemAction {

	static final int BYCLASS =  0;
	static final int BYOBJECT =  1;

	private GSegment background_;
	
	int type;
	
	private KinObject object;


	public Menu( GWindow window )
	{

		super(window);

		background_ = new GSegment();
		GStyle backgroundStyle = new GStyle();
		backgroundStyle.setBackgroundColor (new GColor (0.0f, 0.0f, 0.0f, 0.5f));
		background_.setStyle (backgroundStyle);
		addSegment (background_);



		// Default viewport equals window
		int  viewportWidth  = window.getWidth()/4;
		int  viewportHeight = window.getHeight();
		viewport_ = new GViewport (0, 0, viewportWidth, viewportHeight);


		// Default world extent equals window
		double w0[] = {0.0, 0.0};
		double w1[] = {1.0, 0.0};
		double w2[] = {0.0, 1.0};
		initialWorldExtent_ = new GWorldExtent (w0, w1, w2);
		currentWorldExtent_ = new GWorldExtent (w0, w1, w2);

		// Create transformer instance
		transformer_ = new GTransformer (viewport_, currentWorldExtent_);

	}


	public static double[] createRectangle (double x0, double y0, double width, double height)
	{
		return new double[] {
				x0,         y0,
				x0 + width, y0,
				x0 + width, y0 + height,
				x0,         y0 + height,
				x0,         y0 };
	}

	public double getWidth() {
		return currentWorldExtent_.getWidth();
	}

	public double getHeight() {
		return currentWorldExtent_.getHeight();
	}

	public double getX0() {
		return currentWorldExtent_.get(0)[0];
	}

	public double getY0() {
		return currentWorldExtent_.get(0)[1];
	}
	
	
	RectD getScrollableArea(){
		return new RectD( 0.0, 0.1, 1.0, 1.0 );
	}
	
	double[] getTitleArea(){
		return new double[] {0.0, 0.0,
				1.0, 0.0,
				1.0, 0.1,
				0.0, 0.1,
				0.0, 0.0};
	}


	public void draw()
	{	
		// Draw background
		background_.setGeometryXy (createRectangle (getX0(), getY0(), getWidth(), getHeight()));
	}


	public void populate(KinObject object) {
		
		removeAll();
		
		this.object = object;
		this.type = BYCLASS;

		Map<Class<?>, Integer > childByClasses = new HashMap<Class<?>, Integer>();
		
		
		for( KinObject children : object.getChildren() ){
			
			Class<?> key = children.getClass();
			
			if ( !childByClasses.containsKey(key) ){
				childByClasses.put( key,  1 );
			}
			else {
				Integer n = childByClasses.get( key  );
				childByClasses.put( key,  ++n );
			}
		}
		
		for( Entry<Class<?>, Integer> entry : childByClasses.entrySet() ){
			GColor color = GColor.darkGray; 
			
			int nElt = entry.getValue();
			Class<?> c = entry.getKey();
			if ( nElt == 1 ){
				KinObject child = (KinObject) object.findObject(c);
				if ( (child.getName() == null) || child.getName().isEmpty() ){
					child.setName( c.getSimpleName()  );
				}
				
				if ( child instanceof IColorProvider ){
					Color acolor = ((IColorProvider)child).getColor();
					color = new GColor( acolor.getRed(), acolor.getGreen(), acolor.getBlue(), acolor.getAlpha() );
				}
				String label = child.getName();
				MenuItem item = new ObjectMenuItem( label , color );
				item.setUserData( child );
				add( item );
			}
			else {
				String label = entry.getKey().getSimpleName() + "  (" + entry.getValue() + ")";
				MenuItem item = new ClassMenuItem( label , color );
				item.setUserData( entry.getKey() );
				add( item );
			}
			
		}


		MenuTitle title = new MenuTitle( object.getClass().getSimpleName(), new GColor(128, 128, 128 ));
		add(title);

	}
	
	
	@SuppressWarnings("unchecked")
	public void populate( Class<?> c ){
		
		removeAll();
		
		this.type = BYOBJECT;

		List<?> objects = object.findObjects(c);

		int index = 0;
		for( KinObject children : (List<KinObject>)objects ){
			GColor color = GColor.darkGray; 
			if ( ( children.getName() == null) || children.getName().isEmpty() ){
				children.setName( c.getSimpleName() + "_" + index );
			}
			
			if ( children instanceof IColorProvider ){
				Color acolor = ((IColorProvider)children).getColor();
				color = new GColor( acolor.getRed(), acolor.getGreen(), acolor.getBlue(), acolor.getAlpha() );
			}
			String label = children.getName();
			MenuItem item = new ObjectMenuItem( label , color );
			item.setUserData( children );
			add( item );

			index++;
		}

		
		String titleName = object.getName() + " (" + c.getSimpleName() + ")";
		MenuTitle title = new MenuTitle( titleName, new GColor(128, 128, 128 ));
		add(title);

	}




	public void scrollDown() {
		for( GObject gobject : getChildren() ){
			if ( gobject instanceof MenuItem ){
				MenuItem item = (MenuItem)gobject;
				item.scrollDown();
			}
		}

	}

	public void scrollUp() {
		for( GObject gobject : getChildren() ){
			if ( gobject instanceof MenuItem ){
				MenuItem item = (MenuItem)gobject;
				item.scrollUp();
			}
		}

	}


	@Override
	public void clicked(MenuItem item) {
		if (item instanceof ClassMenuItem ){
			Class<?> c = (Class<?>)item.getUserData();
			populate(c);
		}
		if ( item instanceof ObjectMenuItem ){
			KinObject object = (KinObject)item.getUserData();
			if ( object.getNChildren() > 0 ){
				populate(object);
			}
		}
		redraw();
	}


	@Override
	public void keyPressed(GKeyEvent event) {

		if ( event.getKeyCode() == GKeyEvent.VK_ESCAPE ){
			switch( type ){
			case BYCLASS:
				if ( object.getParent() != null ){
					object = object.getParent();
					populate(object);
				}
				break;
			case BYOBJECT:
				populate(object);
				break;
			}
			redraw();
		}
		

	}
	
	
	
}
