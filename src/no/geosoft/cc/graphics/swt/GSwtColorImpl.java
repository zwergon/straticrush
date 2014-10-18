package no.geosoft.cc.graphics.swt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.interfaces.IColorImpl;

public class GSwtColorImpl implements IColorImpl {

	private GColor color;
	
	public GSwtColorImpl(GColor color) {
		this.color = color;
	}
	
	public Color toSwtColor( Device device ){
		return new Color( device, color.getRed(), color.getGreen(), color.getBlue() );
	}

}
