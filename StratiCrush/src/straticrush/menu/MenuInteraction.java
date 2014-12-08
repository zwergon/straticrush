package straticrush.menu;



import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;


public class MenuInteraction  implements GInteraction {
	
	private Menu menu;
	
	public MenuInteraction( Menu menu ) {
		this.menu = menu;
	}
	
	@Override
	public void event(GScene scene, GEvent event) {

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

		case GEvent.MOTION:
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof MenuItem ){
					MenuItem item = (MenuItem)gobject;
					GSegment highlight = new GSegment();
					GStyle highlightStyle  = new GStyle();
					highlightStyle.setBackgroundColor (new GColor(1.0f, 1.0f, 1.0f, 0.7f));
					highlight.setStyle (highlightStyle);
					interaction.addSegment (highlight);
					highlight.setGeometry ( item.getBorder() );
				}

			}
			break;
			
		case GEvent.WHEEL_MOUSE_DOWN:
			menu.scrollDown();
			break;
			
		case GEvent.WHEEL_MOUSE_UP:
			menu.scrollUp();
			break;
			
			
		case GEvent.ABORT:
			scene.remove(interaction);
			interaction = null;
			default:
				break;
		}


		scene.refresh();
		
	}

}
