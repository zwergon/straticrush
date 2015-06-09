package no.geosoft.graphics.factory;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.graphics.GL.GGLBitmapFont;
import no.geosoft.cc.graphics.GL.GGLCanvas;
import no.geosoft.cc.graphics.GL.GGLFontImpl;
import no.geosoft.cc.graphics.GL.GGLImage;
import no.geosoft.cc.graphics.GL.GGLNewtCanvas;
import no.geosoft.cc.graphics.GL.GGLSwtCanvas;
import no.geosoft.cc.graphics.swt.GSwtCanvas;
import no.geosoft.cc.graphics.swt.GSwtColorImpl;
import no.geosoft.cc.graphics.swt.GSwtFontImpl;
import no.geosoft.cc.graphics.swt.GSwtImageImpl;
import no.geosoft.cc.interfaces.ICanvas;
import no.geosoft.cc.interfaces.IColorImpl;
import no.geosoft.cc.interfaces.IFontImpl;
import no.geosoft.cc.interfaces.IImageImpl;

public class GFactory {
	
	static public ICanvas createCanvas( Object parent, GWindow window ){
		//return new GSwtCanvas(parent, window);
		//return new GGLSwtCanvas(parent, window);
		//return new GGLCanvas(parent, window);
		return new GGLNewtCanvas(parent, window);
	}
	
	static public IColorImpl createColor( GColor color ){
		return new GSwtColorImpl( color );
		//return null;
	}
	
	static public IFontImpl createFont( GFont gfont ){
	    return new GGLFontImpl( gfont, true);
		//return new GSwtFontImpl(gfont);
		//return new GGLBitmapFont();
	}
	
	static public IImageImpl createImage( GImage image ){
		//return new GSwtImageImpl( image );
		return new GGLImage( image );
	}
}