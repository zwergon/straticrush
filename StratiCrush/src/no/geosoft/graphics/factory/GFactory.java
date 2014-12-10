package no.geosoft.graphics.factory;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.interfaces.ICanvas;
import no.geosoft.cc.interfaces.IColorImpl;
import no.geosoft.cc.interfaces.IFontImpl;
import no.geosoft.cc.interfaces.IImageImpl;

public class GFactory implements IGFactoryImpl {

    static GFactory instance = null;
    
    IGFactoryImpl impl;

    static public GFactory getInstance() {
        if (null == instance) {
            instance = new GFactory();
        }
        return instance;
    }
    
    public void setImpl( IGFactoryImpl impl ){
        this.impl = impl;
    }

    public ICanvas createCanvas(Object parent, GWindow window) {
        return impl.createCanvas(parent, window);
        //return new GSwtCanvas(parent, window);
        //return new GGLSwtCanvas(parent, window);
    }

    public IColorImpl createColor(GColor color) {
        return impl.createColor(color);
        //return new GSwtColorImpl( color );
        //return null;
    }

    public IFontImpl createFont(GFont gfont) {
        return impl.createFont(gfont);
        //return new GGLFontImpl( gfont, true);
        //return new GSwtFontImpl(font);
        //return new GGLBitmapFont();
    }

    public IImageImpl createImage(GImage image) {
        return impl.createImage(image);
        //return new GSwtImageImpl( image );
        //return new GGLImage( image );
    }
}
