package no.geosoft.cc.graphics.GL;

import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.interfaces.IImageImpl;

public class GGLImage implements IImageImpl {
	
	public GImage parent_;
	
	public GGLImage( GImage image ) {
		parent_ = image;
	}

}
