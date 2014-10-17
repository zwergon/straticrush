package straticrush.menu;

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
	    double w0[] = {0.0, (double) viewportHeight};
	    double w1[] = {(double) viewportWidth,   (double) viewportHeight};
	    double w2[] = {0.0, 0.0};
	    initialWorldExtent_ = new GWorldExtent (w0, w1, w2);
	    currentWorldExtent_ = new GWorldExtent (w0, w1, w2);
	    
	    // Create transformer instance
	    transformer_ = new GTransformer (viewport_, currentWorldExtent_);
	   
	  }
	  

	  public static int[] createRectangle (int x0, int y0, int width, int height)
	  {
	    return new int[] {x0,               y0,
	                      x0 + (width - 1), y0,
	                      x0 + (width - 1), y0 + (height - 1),
	                      x0,               y0 + (height - 1),
	                      x0,               y0};
	  }
	  
		public int getWidth() {
			return (int)viewport_.getWidth();
		}

		public int getHeight() {
			return (int)viewport_.getHeight();
		}
		
		public int getX0() {
			return viewport_.getX0();
		}

		public int getY0() {
			return viewport_.getY0();
		}


	  public void draw()
	  {	
		  // Draw background
		  background_.setGeometry (createRectangle (viewport_.getX0(), viewport_.getY0(), (int)viewport_.getWidth(), (int)viewport_.getHeight()));
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
