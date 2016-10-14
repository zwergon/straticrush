package no.geosoft.cc.graphics.swt;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.interfaces.IImageImpl;
import no.geosoft.cc.utils.Rect;

public class GSwtImageImpl implements IImageImpl {
	
	private GImage image;
	private Image simage = null;
	
	public GSwtImageImpl( GImage image ){
		this.image = image;
	}
	
	public Image toSwtImage( Device device ){
		
		if ( null == simage ){
			GStyle actualStyle = image.getActualStyle();
			Rect rect = image.getRectangle();
			int[] imageData = image.getImageData();

			GColor bg = actualStyle.getBackgroundColor();
			GColor fg = actualStyle.getForegroundColor();


			PaletteData paletteData = new PaletteData(
					new RGB[] {
							new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()), 
							new RGB(fg.getRed(), fg.getGreen(), fg.getBlue())});

			ImageData data = new ImageData(rect.width, rect.height, 1, paletteData );
			simage = new Image( device, data );

			int pointNo = 0;
			for (int i = 0; i < rect.height; i++) {
				for (int j = 0; j < rect.width; j++) {
					data.setPixel (i, j, imageData[pointNo]);
					pointNo++;
				}
			}
		}
		
		return simage;
	}

}
