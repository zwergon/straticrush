package no.geosoft.cc.graphics.GL;


import no.geosoft.cc.graphics.GTooltipInfo;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.interfaces.ICanvas;
import no.geosoft.cc.interfaces.ITooltip;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class GGLTooltip implements ITooltip {
	
	GWindow window;
	
	Shell popup;
	
	GTooltipInfo info;

	public GGLTooltip(GWindow window, GTooltipInfo info) {
		this.window = window;
		this.info = info;
	}
	
	@Override
	public boolean isVisible(){
		return (popup != null) && !popup.isDisposed();
	}

	@Override
	public void show() {
		if ( popup == null ){
			
			Display display = Display.getDefault();
			popup = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP | SWT.MODELESS | SWT.NO_FOCUS);
			
			RowLayout rowLayout = new RowLayout();
			rowLayout.wrap = false;
			rowLayout.pack = false;
			rowLayout.justify = true;
			rowLayout.marginLeft = 5;
			rowLayout.marginTop = 5;
			rowLayout.marginRight = 5;
			rowLayout.marginBottom = 5;
			popup.setBackground( display.getSystemColor(SWT.COLOR_YELLOW) );
			
			popup.setLayout(rowLayout);
			Label label = new Label(popup, SWT.NONE);
			
			label.setBackground( display.getSystemColor(SWT.COLOR_YELLOW) );
			label.setText(info.getInfo());
			
			popup.pack();
			popup.open();
		}
		
		ICanvas canvas = window.getCanvas();
		int h = canvas.getHeight();
		int y = canvas.getY();
		popup.setLocation( canvas.getX() + info.getX(), h - info.getY() + y );
	}

	@Override
	public void hide() {
		if (isVisible())
		{
			popup.close();
			popup = null;
		}

	}

}
