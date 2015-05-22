package straticrush.menu;

import no.geosoft.cc.graphics.GKeyEvent;

public interface IMenuItemAction {
	void clicked( MenuItem item );
	void keyPressed( GKeyEvent event );
}
