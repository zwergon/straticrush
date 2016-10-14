package no.geosoft.cc.graphics.swt;


import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.interfaces.ICanvas;
import no.geosoft.cc.utils.GRegion;
import no.geosoft.cc.utils.Rect;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class GSwtCanvas  extends Composite 
implements ICanvas,
MouseListener,
MouseWheelListener,
MouseMoveListener {

	private GWindow window_;
	private Image backBuffer = null;
	private GC gc = null;
	private int mouseMask = 0;

	public GSwtCanvas(Object parent, GWindow window ) {


		super((Composite)parent, SWT.NONE | SWT.NO_BACKGROUND );


		//associate to GWindow
		window_ = window;

		addListener( SWT.Resize,  new Listener(){
			@Override
			public void handleEvent(Event event) {
				handleResizeEvent( event );	
			}
		});


		addListener( SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handlePaintEvent( event );
			}
		});

		addMouseListener( this );
		addMouseMoveListener(this);
		addMouseWheelListener( this);

	}

	protected void handlePaintEvent(Event event) {

		if ( null != backBuffer ){
			event.gc.drawImage(backBuffer, 
					0, 0, backBuffer.getBounds().width, backBuffer.getBounds().height, 
					0, 0,backBuffer.getBounds().width, backBuffer.getBounds().height );
		}
	}
	
	@Override
	public void setBackgroundColor(GColor color) {
		//TODO set background color
	};

	@Override
	public int getX() {
		return getClientArea().x;
	}

	@Override
	public int getY() {
		return getClientArea().y;
	}

	@Override
	public int getWidth() {
		return getSize().x;
	}

	@Override
	public int getHeight() {
		return getSize().y;
	}
	
	/**
	 * Refresh this canvas.
	 */
	public void initRefresh()
	{
		//do nothing
	}

	@Override
	public void refresh() {
		redraw();
	}

	@Override
	public void setClipArea(GRegion damageRegion) {
		Rect clip = damageRegion.getExtent();
		gc.setClipping(clip.x, clip.y, clip.width, clip.height);

	}

	@Override
	public void clear(Rect clip) {
		gc.fillRectangle(clip.x, clip.y, clip.width, clip.height);
	}

	@Override
	public void render( GSegment segment, GStyle style) {

		
		int[] x = segment.getX();
		int[] y = segment.getY();
		int[] xy = new int[2*x.length];
		
		for( int i = 0; i<x.length; i++ ){
			xy[2*i] = x[i];
			xy[2*i+1] = y[i];
		}

		GColor bg = style.getBackgroundColor();
		if ( null != bg ){

			GSwtColorImpl color = (GSwtColorImpl)bg.getImpl();
			
			Color c = color.toSwtColor(getDisplay());
			gc.setBackground( c );
			gc.setForeground( c );
			gc.fillPolygon(xy);
			c.dispose();
		}

		GColor fg = style.getForegroundColor();
		if ( style.isLineVisible() ){
			GSwtColorImpl color = (GSwtColorImpl)fg.getImpl();
			Color c = color.toSwtColor(getDisplay());
			gc.setForeground( c );
			gc.drawPolygon(xy);
			c.dispose();
		}
	}



	@Override
	public void render(GText text, GStyle style) {

		GSwtColorImpl fg = (GSwtColorImpl)style.getForegroundColor().getImpl();
		Color c = fg.toSwtColor(getDisplay());
		
		GSwtFontImpl fontImpl = (GSwtFontImpl)style.getFont().getImpl();
		Font font = fontImpl.toSwtFont(getDisplay());

		Rect rectangle = text.getRectangle();

		Point size = gc.textExtent(text.getText());
		double textWidth  = size.x;
		double textHeight = size.y;

		int x = rectangle.x +
				(int) Math.round ((rectangle.width - textWidth) / 2.0) ;
		int y = rectangle.y +
				(int) Math.round ((rectangle.height - textHeight) / 2.0) ;

		gc.setBackground(getBackground()); 
		gc.setForeground(c); 
		gc.setFont(font); 
		gc.drawText(text.getText(), x, y ); 
	
		c.dispose();
		font.dispose(); 

	}

	@Override
	public void render(GImage image) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(int[] x, int[] y, GImage image) {
		Rect rectangle = image.getRectangle();

		int w2 = rectangle.width/2;
		int h2 = rectangle.height/2;
		
		GSwtImageImpl impl = (GSwtImageImpl)image.getImpl();
		Image simage = impl.toSwtImage(getDisplay());
		for( int i=0; i< x.length; i++ ){
			for( int j=0; j< y.length; j++ ){
				gc.drawImage( simage, x[i]-w2, y[i]-h2);
			}
		}
	
	}

	@Override
	public Rect getStringBox(String string, GFont gfont) {

		GSwtFontImpl fontImpl = (GSwtFontImpl)gfont.getImpl();
		Font font = fontImpl.toSwtFont(getDisplay());
		
		gc.setFont(font);
		Point extent = gc.textExtent(string);
		font.dispose();

		return new Rect( 0, 0, extent.x, extent.y );
	}

	private boolean createBackBuffer( int width, int height ){

		if (width <= 0 || height <= 0) return false;

		if ( null != backBuffer ){
			if ( ( width == backBuffer.getBounds().width ) &&
					( height == backBuffer.getBounds().height ) ){
				return true;
			}

			gc.dispose();
			backBuffer.dispose();
		}


		backBuffer = new Image(getDisplay(), width, height );
		gc = new GC(backBuffer);


		gc.setBackground(getBackground());
		gc.setForeground(getBackground());
		gc.fillRectangle(0,0, width, height);

		return true;
	}

	private void handleResizeEvent(Event event) {
		
		if ( createBackBuffer(getWidth(), getHeight() ) ){
			window_.resize();
		}
		
	}


	/**
	 * map modifiers from AWT event to GEvent modifiers
	 */
	private void setModifiers( MouseEvent event, GMouseEvent gevent ){
		/*		  gevent.modifier = GEvent.NONE;
		  if ( event.stateMask & SWT.CTRL ){
			  gevent.setModifier( GEvent.ALT_DOWN_MASK, true );
		  }
		  if ( event.isControlDown() ){
			  gevent.setModifier( GEvent.CTRL_DOWN_MASK, true );
		  }
		  if ( event.isMetaDown() ){
			  gevent.setModifier( GEvent.META_DOWN_MASK, true );
		  }
		 */
	}



	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseScrolled( MouseEvent event ) {

		GMouseEvent gevent = new GMouseEvent( event.x,  event.y );

		int notches = event.count;
		if (notches > 0) {
			gevent.type = GMouseEvent.WHEEL_MOUSE_UP;
		} else {
			gevent.type = GMouseEvent.WHEEL_MOUSE_DOWN;
		}
		setModifiers(event, gevent);
		window_.wheelMoved( gevent );
	}

	/**
	 * Method called when the pointer enters this window. If an interaction
	 * is installed, pass a FOCUS_IN event to it.
	 * 
	 * @param event  Mouse event trigging this method.
	 */
	/*public void mouseEntered (MouseEvent event)
	  {
	    window_.mouseEntered (event.getX(), getHeight() - event.getY());
	  }*/


	/**
	 * Method called when the pointer exits this window. If an interaction
	 * is installed, pass a FOCUS_OUT event to it.
	 * 
	 * @param event  Mouse event trigging this method.
	 */
	/*public void mouseExited (MouseEvent event)
	  {
	    window_.mouseExited (event.getX(), getHeight() - event.getY());
	  }*/

	@Override
	public void mouseMove(MouseEvent event) {

		if ( mouseMask == 0  ){
			window_.mouseMoved( event.x, event.y );
			return;
		}

		int modifiers = event.stateMask;
		GMouseEvent gevent = new GMouseEvent( event.x, event.y );

		if ( ( mouseMask & SWT.BUTTON1 ) == SWT.BUTTON1) {
			gevent.type = GMouseEvent.BUTTON1_DRAG;
		}
		else if (( mouseMask & SWT.BUTTON2 ) == SWT.BUTTON2)  {
			gevent.type = GMouseEvent.BUTTON2_DRAG;
		}
		else
			gevent.type = GMouseEvent.BUTTON3_DRAG;


		setModifiers(event, gevent);
		window_.mouseDragged ( gevent );

	}

	@Override
	public void mouseDown(MouseEvent event) {
		int modifiers = event.stateMask;
		GMouseEvent gevent = new GMouseEvent( event.x, event.y );

		if ( event.button == 1 ) {
			gevent.type = GMouseEvent.BUTTON1_DOWN;
		}
		else if (event.button == 2 ) {
			gevent.type = GMouseEvent.BUTTON2_DOWN;
		}
		else
			gevent.type = GMouseEvent.BUTTON3_DOWN;

		setModifiers(event, gevent);

		window_.mousePressed ( gevent );

		if ( event.button == 1 ) {
			mouseMask |= SWT.BUTTON1;
		}
		else if (event.button == 2 ) {
			mouseMask |= SWT.BUTTON2;
		}
		else
			mouseMask |= SWT.BUTTON3;

	}

	
	@Override
	public void mouseUp(MouseEvent event) {


		int modifiers = event.stateMask;
		GMouseEvent gevent = new GMouseEvent( event.x, event.y );

		if ( event.button == 1 ) {
			gevent.type = GMouseEvent.BUTTON1_UP;
			mouseMask &= ~SWT.BUTTON1;
		}
		else if (event.button == 2 ) {
			gevent.type = GMouseEvent.BUTTON2_UP;
			mouseMask &= ~SWT.BUTTON2;
		}
		else {
			gevent.type = GMouseEvent.BUTTON3_UP;
			mouseMask &= ~SWT.BUTTON3;
		}

		setModifiers(event, gevent);

		window_.mouseReleased( gevent );

	}


}
