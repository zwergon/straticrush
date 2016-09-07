package straticrush.menu;



import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GWindow;
import straticrush.view.Plot;
import straticrush.view.StratiWindow;
import straticrush.view.View;
import fr.ifp.kronosflow.model.KinObject;


public class MenuInteraction  implements GInteraction {
	
	private Menu menu;
	
	private View view;
	
	public MenuInteraction( Menu menu ) {
		this.menu = menu;
	}
	
	@Override
	public void event(GScene scene, GMouseEvent event) {

		if (!( scene instanceof Menu ) ){
			return;
		}
		
		GObject interaction = scene.find ("interaction");
		if (interaction == null) {
			interaction = new GObject("interaction");
			scene.add (interaction);
		}
		
		interaction.removeSegments();

		switch (event.type) {

		case GMouseEvent.MOTION:
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof MenuItem ){
					MenuItem item = (MenuItem)gobject;
					createSelectedView(item);
					GSegment highlight = new GSegment();
					GStyle highlightStyle  = new GStyle();
					highlightStyle.setBackgroundColor (new GColor(1.0f, 1.0f, 1.0f, 0.7f));
					highlight.setStyle (highlightStyle);
					interaction.addSegment (highlight);
					highlight.setGeometry ( item.getBorder() );
				}

			}
			break;
			
			
		case GMouseEvent.BUTTON1_DOWN:
			selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof MenuItem ){
						menu.clicked( (MenuItem) gobject );
				}
			}
			break;
			
		case GMouseEvent.WHEEL_MOUSE_DOWN:
			menu.scrollDown();
			break;
			
		case GMouseEvent.WHEEL_MOUSE_UP:
			menu.scrollUp();
			break;
			
			
		case GMouseEvent.FOCUS_OUT:
			removeSelectedView();
			break;
			
			
		case GMouseEvent.ABORT:
			scene.remove(interaction);
			removeSelectedView();
			interaction = null;
			default:
				break;
		}


		scene.refresh();
		
	}

	
	
	public void keyEvent( GKeyEvent event ){
		switch( event.type ){
		case GKeyEvent.KEY_PRESSED:
			menu.keyPressed(event);
			removeSelectedView();
			break;
		}
		menu.refresh();
	}
	
	
	private void createSelectedView( MenuItem item ){
			
		KinObject object = null;
		if ( item instanceof ObjectMenuItem ){
			 object = (KinObject)((ObjectMenuItem)item).getUserData();
		}
		//already displayed object
		if ( ( view != null ) && ( view.getUserData() == object ) ){
			return;
		}
		
		Plot plot = getPlot();
		if ( null == plot ){
			return;
		}

		if ( view != null ){	
			plot.destroyView( view );
			view = null;
		}
		
		if ( object != null ){	
			view = plot.createView( object );
			if ( null != view ){
				view.useSelectedStyle();
			}
		}
		
		plot.refresh();
		
	}
	
	private void removeSelectedView() {
		if ( view != null ){

			Plot plot = getPlot();
			if ( null == plot ){
				return;
			}
				
			if ( view != null ){	
				plot.destroyView(view);
				view = null;
			}
			plot.refresh();
		}
	}
	
	private Plot getPlot(){
		GWindow window = menu.getWindow();
		Plot scene = null;

        for( GScene sc : window.getScenes() ){
            if ( sc instanceof Plot ) {
                scene = (Plot)sc;
                break;
            }
        }

        return scene;
	}

}
