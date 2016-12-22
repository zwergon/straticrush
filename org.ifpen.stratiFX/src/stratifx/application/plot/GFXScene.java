package stratifx.application.plot;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFont;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GRect;
import stratifx.canvas.graphics.GRegion;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.GText;
import stratifx.canvas.graphics.GWorldExtent;
import stratifx.canvas.graphics.ICanvas;

public class GFXScene extends GScene implements ICanvas {
	
	Canvas canvas;
	
    public GFXScene( Canvas canvas, GWorldExtent extent ) {
    	
    	this.canvas = canvas;

    	Bounds localB = canvas.getLayoutBounds();
    	initialize(
    			this,
    			new GRect( 
    					(int)localB.getMinX(),  (int)localB.getMinY(), 
    					(int)localB.getWidth(), (int)localB.getHeight() ),
    			extent
    			);

	}
	
	public GFXScene( Canvas canvas ) {

		this.canvas = canvas;

		Bounds localB = canvas.getLayoutBounds();
		initialize(
				this,
				new GRect( 
						(int)localB.getMinX(),  (int)localB.getMinY(), 
						(int)localB.getWidth(), (int)localB.getHeight() )
				);

	}
	
	@Override
	public void setClipArea(GRegion damageRegion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear( GRect extent ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackgroundColor(GColor color) {
		// TODO Auto-generated method stub
	}

	@Override
	public void render( GSegment segment, GStyle style ) {
		
		double[][] xy = segment.getXY();

		GraphicsContext gc = canvas.getGraphicsContext2D();
		GColor bg = style.getBackgroundColor();
		if ( null != bg ){

			float[] rgba = new float[3];
			bg.getRGBColorComponents(rgba);
			Color color = new Color(rgba[0], rgba[1], rgba[2], 1. );
			gc.setFill( color );
			gc.fillPolygon( xy[0], xy[1], segment.size() );
			
		}

		GColor fg = style.getForegroundColor();
		if ( style.isLineVisible() ){
			float[] rgba = new float[3];
			fg.getRGBColorComponents(rgba);
			Color color = new Color(rgba[0], rgba[1], rgba[2], 1. );
			gc.setStroke( color );
			gc.setLineWidth(1);
			gc.strokePolygon( xy[0], xy[1], segment.size()  );	
		}

	}

	@Override
	public void render( GText text, GStyle style ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(GImage image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(int[] x, int[] y, GImage image) {
		// TODO Auto-generated method stub

	}
	@Override
	public GRect getStringBox(String string, GFont font) {
		// TODO Auto-generated method stub
		return null;
	}


}
