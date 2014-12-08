package straticrush.menu;

import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.topology.Contact;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GTransformer;
import no.geosoft.cc.graphics.GViewport;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.graphics.GWorldExtent;

public class Menu extends GScene {


	private GSegment background_;
	private GSegment title;


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
		System.out.println("X,Y " + getX0() + "," + getY0() );
		background_.setGeometryXy (createRectangle (getX0(), getY0(), getWidth(), getHeight()));
	}


	public void populate(KinObject object) {

		
		int index = 0;
		int nChildren = object.getNChildren();
		for( KinObject children : object.getChildren() ){

			String label = null;
			if ( children instanceof Contact ) {
				label = "Contact";
			}
			else {
				label = "Patch";
			}

			label +=  " " + index++;

			GColor color = new GColor( (int)(255*(double)index/(double)nChildren), (int)(255*(double)(nChildren-index)/(double)nChildren), 0, 255  );
			MenuItem item = new MenuItem( label , color );
			item.setUserData( children );
			add( item );
		}


		MenuTitle title = new MenuTitle("Titre", new GColor(128, 128, 128 ));
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



}
