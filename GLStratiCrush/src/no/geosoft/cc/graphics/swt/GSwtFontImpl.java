package no.geosoft.cc.graphics.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;

import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.interfaces.IFontImpl;

public class GSwtFontImpl implements IFontImpl {
		
	GFont gfont;
	
	public GSwtFontImpl( GFont gfont ){
		this.gfont = gfont;
	}

	public Font toSwtFont( Device device ){
		int swtFontStyle;
		if ( gfont.isBold() ){
			swtFontStyle = SWT.BOLD;
		}
		else {
			swtFontStyle = SWT.NORMAL;
		}
		
		if ( gfont.isItalic() ){
			swtFontStyle |= SWT.ITALIC;
		}
			
		
		return new Font( device, gfont.getFontName(), (int)gfont.getSize(), swtFontStyle );
	}
	
}
