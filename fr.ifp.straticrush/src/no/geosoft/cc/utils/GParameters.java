package no.geosoft.cc.utils;

import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.style.StyleManager;


/**
 * Singleton to store parameters used in application.
 * @author lecomtje
 *
 */
public class GParameters {
	
	Style style;
	
	static private GParameters instance = new GParameters();
		
	private GParameters(){
		style = StyleManager.getInstance().createStyle();
		style.setAttribute( "nam", "GParameters");
	}

	static public Style getStyle(){
		return instance.style;
	}
	
}
