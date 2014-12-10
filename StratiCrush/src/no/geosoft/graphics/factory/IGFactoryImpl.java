package no.geosoft.graphics.factory;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.interfaces.ICanvas;
import no.geosoft.cc.interfaces.IColorImpl;
import no.geosoft.cc.interfaces.IFontImpl;
import no.geosoft.cc.interfaces.IImageImpl;

/**
 *
 * @author lecomtje
 */
public interface IGFactoryImpl {

    public ICanvas createCanvas(Object parent, GWindow window);
    public IColorImpl createColor(GColor color);
    public IFontImpl createFont(GFont gfont);
    public IImageImpl createImage(GImage image);

}
