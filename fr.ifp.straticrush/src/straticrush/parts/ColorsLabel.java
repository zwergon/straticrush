package straticrush.parts;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GColorMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ColorsLabel extends Canvas implements PaintListener {
	
	GColorMap colormap;

	public ColorsLabel(Composite parent, GColorMap colormap) {
		super(parent, SWT.NONE);
		
	
		this.colormap = colormap;
		
		addPaintListener(this);
	}
	
	

	@Override
	public void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		
		Rectangle rect = getClientArea();
		
		int w = rect.width;
		int h = rect.height;
		
		double cmin = colormap.getMin();
		double cmax = colormap.getMax();
		
		double step = ( cmax - cmin ) / (double) (h-1.);
		
		double value = cmin;
		for( int i=0; i<h; i++ ){
			GColor color = colormap.getColor(value);
			gc.setForeground( new Color( getDisplay(), color.getRed(), color.getGreen(), color.getBlue() ));
			gc.drawLine(0,  h-i, w, h-i);
			value += step;
		}
		
	}
	
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = 50;
		int height = colormap.getNColors();
		return new Point(width, height);
	}

}
