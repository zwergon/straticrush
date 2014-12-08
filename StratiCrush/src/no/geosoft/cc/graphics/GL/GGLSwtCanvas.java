package no.geosoft.cc.graphics.GL;



import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;
 

import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GComponent;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.graphics.swt.GSwtColorImpl;
import no.geosoft.cc.graphics.swt.GSwtFontImpl;
import no.geosoft.cc.interfaces.ICanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.jogamp.opengl.util.gl2.GLUT;

import fr.ifp.kronosflow.geometry.Rect;
import fr.ifp.kronosflow.geometry.Region;


public class GGLSwtCanvas extends Composite 
	implements  
		ICanvas,
		MouseListener,
		MouseWheelListener,
		MouseMoveListener
	{
	
	
	
	private final GWindow    window_;
	
	/** Widget that displays OpenGL content. */
    protected GLCanvas glcanvas;

    /** Used to get OpenGL object that we need to access OpenGL functions. */
    protected GLContext glcontext;
    
	private Rect             cleared_;
	private GLUT             glut_;
	
	private int mouseMask = 0;
	
	private List<GLAction> queue= new ArrayList<GGLSwtCanvas.GLAction>(16);
	
	private GColor backgroundColor;
	
	public GGLSwtCanvas( Object parent, GWindow window ) {
		
		super( (Composite)parent, SWT.NONE | SWT.NO_BACKGROUND );
		
		backgroundColor = GColor.WHITE;
	
		setLayout(new FillLayout());
		
		GLProfile glprofile = GLProfile.get( GLProfile.GL2 );

		GLData gldata = new GLData();
		gldata.doubleBuffer = true;
		gldata.stencilSize = 8;
		glcanvas = new GLCanvas( this, SWT.NONE | SWT.NO_BACKGROUND, gldata );
		glcanvas.setCurrent();
		glcontext = GLDrawableFactory.getFactory( glprofile ).createExternalGLContext();
		
		glcanvas.setFocus();

		glcanvas.addListener( SWT.Resize, new Listener() {
			public void handleEvent( Event event ) {
				glcanvas.setCurrent();
				glcontext.makeCurrent();
				GL2 gl = glcontext.getGL().getGL2();
				reshape( gl );
				glcontext.release();
			}
		});
		
		glcanvas.addListener( SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				execute();				
			}
		});
		

		glcontext.makeCurrent();
        GL2 gl2 = glcontext.getGL().getGL2();
        gl2.setSwapInterval( 0 );
        gl2.glEnable( GL.GL_SCISSOR_TEST );
        gl2.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );
		gl2.glDisable(GL.GL_DEPTH_TEST);
		glcontext.release();
		
	
		
		//associate to GWindow
		window_ = window;
				
		glut_ = new GLUT();
		
		glcanvas.addMouseListener( this );
		glcanvas.addMouseMoveListener(this);
		glcanvas.addMouseWheelListener( this);

	}
	
	public void setBackgroundColor(GColor color) {
		backgroundColor = color;
	};
	
	@Override
	public int getWidth() {
		Rectangle rectangle = glcanvas.getClientArea();
		return rectangle.width;
	}



	@Override
	public int getHeight() {
		Rectangle rectangle = glcanvas.getClientArea();
		return rectangle.height;
	}

	@Override
	public void dispose() {
		glcanvas.dispose();
		super.dispose();

	}


	public void reshape(GL2 gl2) {
				
		Rectangle rectangle = glcanvas.getClientArea();
		int iWidth = rectangle.width;
		int iHeight = Math.max( rectangle.height, 1 );
		
		gl2.glMatrixMode( GLMatrixFunc.GL_PROJECTION );
		gl2.glLoadIdentity();

		GLU glu = new GLU();
		glu.gluOrtho2D( 0.0f, iWidth, 0.0f, iHeight );

		gl2.glMatrixMode( GLMatrixFunc.GL_MODELVIEW );
		gl2.glViewport( 0, 0, iWidth, iHeight );
		gl2.glLoadIdentity();
	

		cleared_ = new Rect (0, 0, iWidth, iHeight);
		
		window_.resize();

	}
	
	/**
	 * Refresh this canvas.
	 */
	public void initRefresh()
	{
		clear();
	}
	
	/**
	 * Refresh this canvas.
	 */
	public void refresh()
	{
		if ( null != cleared_ ){
			redraw(cleared_.x, cleared_.y, cleared_.width, cleared_.height, true);
		}
		
	}
	
	
	 /**
	   * Render the specified polyline into back buffer using the
	   * specified style.
	   * 
	   * @param x      Polyline x coordinates.
	   * @param y      Polyline y coordinates.
	   * @param style  Style used for rendering.
	   */
	  public void render (GSegment segment, GStyle style )
	  {
		   add( new XYGLaction(segment.getX(), segment.getY(), style) );
	  }
	  
	  /**
	   * Render the specified text element into back buffer using the
	   * specified style.
	   * 
	   * @param text   Text to render.
	   * @param style  Style used for rendering.
	   */
	  public void render (GText text, GStyle style)
	  {
	    add( new TextGLaction(text, style) );
	  }

	  

	  /**
	   * Render the specified image into back buffer.
	   * 
	   * @param image  Image to render.
	   */
	  public void render (GImage image)
	  {
	    //TODO
	  }

	  
	  
	  /**
	   * Render the specified image at every vertex along the specified
	   * polyline.
	   *
	   * @param x      Polyline x components.
	   * @param y      Polyline y components.   
	   * @param image  Image to render.
	   */
	  public void render (int[] x, int[] y, GImage image)
	  {
	   	add( new ImageGLAction(x, y, image) );
	  }

	  

	  /**
	   * Position the specified AWT component within this JPanel.
	   * 
	   * @param component  AWT component to position.
	   */
	  void render (GComponent component)
	  {
	    //TODO
	  }
	  
	  	  
	  /**
	   * Set clip area for upcomming draw operations.
	   * 
	   * @param region  Region to use as clip area.
	   */
	  public void setClipArea (Region region)
	  {
		  add( new ClipGlAction(region) );
	  }
	  
	  /**
	   * Clear the specified area in the back buffer.
	   * 
	   * @param rectangle  Rectangle area to clear in the back buffer.
	   */
	  public void clear (Rect rectangle)
	  {
		  add( new ClearGlAction(rectangle) );
	  }
	  
	  
	  
	  /*************************************************************/
	  
	  
	  public interface GLAction
	  {
	     public void execute(GL target);
	  }
	  
	  private void add(GLAction action)
	  {
		  synchronized (queue) { queue.add(action); }
	  }
	  
	  private void clear(){
		  synchronized (queue)
		  {
			  queue.clear();
		  }
	  }
	  
	  private void execute()
	  {
		  // make a copy of the queue to allow thread safe iteration
		  ArrayList<GLAction> temp = null;
	
		  synchronized (queue)
		  {
			  // Only make a copy, if the queue has entries
			  if( queue.size() != 0 )
				  temp = new ArrayList<GLAction>(queue); 
		  }

		  // iterate outside of the synchronization to avoid blocking the queue
		  if( temp!=null ){
			  
			  glcanvas.setCurrent();
			  glcontext.makeCurrent();
			  GL2 gl = glcontext.getGL().getGL2();
			  gl.glDrawBuffer( GL.GL_BACK );
			  for ( GLAction action : temp ) {
				  action.execute(gl);
			  }
			  glcanvas.swapBuffers();
			  
			  glcontext.release();
			  
		  }
	  }
	  
	  private class ImageGLAction implements GLAction {

		  int[] x;
		  int[] y;
		  GImage image_;

		  public ImageGLAction( int[] x, int[] y, GImage image) {
			  this.image_ = image;
			  this.x = x;
			  this.y = y;
		  }

		  int createTexture(GL2 gl){
 
			  Rect rectangle = image_.getRectangle();
			  
			  int w = rectangle.width;
			  int h = rectangle.height;
			  int s = Math.max( w, h);
			  
			  ByteBuffer brga = ByteBuffer.allocate(w*h*4);
			  byte[] pbrga = brga.array();
			  
			  for( int i = 0; i<w*h; i++ ){
				  pbrga[4*i] = (byte) 255;
				  pbrga[4*i+1]= (byte) 0;
				  pbrga[4*i+2]= (byte) 0;
				  pbrga[4*i+3]= (byte) 255;
			  }
			  
			  
			  IntBuffer texture = IntBuffer.allocate(1);
			  gl.glEnable(GL2.GL_TEXTURE_2D);
			  gl.glGenTextures(1, texture);
			  gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.get(0));
			  gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
			  gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
			  gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		     
			  gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA, s, s, 0, GL2.GL_BGRA, GL.GL_UNSIGNED_BYTE, brga);
			  
			  
			  return texture.get(0);
		  }

		  @Override
		  public void execute(GL target) {
			  GL2 gl = target.getGL2();
			  int item_list = gl.glGenLists(1);
			  gl.glNewList(item_list, GL2.GL_COMPILE );

			  gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			  gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
			  gl.glEnable(GL2.GL_TEXTURE_2D);
			  
			  Rect rectangle = image_.getRectangle();
			  
			  int w2 = rectangle.width/2;
			  int h2 = rectangle.height/2;
			  gl.glBegin(GL2.GL_QUADS);
			  for( int i=0; i< x.length; i++ ){
				  gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2i(x[i]-w2, y[i]-h2);
				  gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2i(x[i]-w2, y[i]+h2);
				  gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2i(x[i]+w2, y[i]+h2);
				  gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2i(x[i]+w2, y[i]-h2);
			  }
			  gl.glEnd();
			  
			  gl.glDisable(GL2.GL_TEXTURE_2D);

			  gl.glEndList();
			  
			  int texture_id = createTexture(gl);
			  gl.glBindTexture(GL.GL_TEXTURE_2D, texture_id);
			  
			  gl.glCallList(item_list);
			  
		  }

	  }
	  
	  /**
	   * GLAction for clipping an area
	   * @author <a href="mailto:Jean-Francois.Lecomte@ifpen.fr">Jef Lecomte</a>
	   *
	   */
	  private class ClipGlAction implements GLAction {
		 Rect clip;
		  
		public ClipGlAction( Region region ) {
			clip = region.getExtent();
		}
		  
		@Override
		public void execute(GL target) {
			GL2 gl = target.getGL2();
			gl.glScissor( clip.x, clip.y, clip.width, clip.height );
		}  
		
	  }
	  private class ClearGlAction implements GLAction {
		  Rect clip;
		  
		public ClearGlAction( Rect region ) {
			clip = region;
		}
		  
		@Override
		public void execute(GL target) {
			GL2 gl2 = target.getGL2();
			gl2.glScissor( clip.x, clip.y, clip.width, clip.height );
			
			float[] colors = backgroundColor.getComponents(null);
			gl2.glClearColor( colors[0], colors[1], colors[2], colors[3]);
			gl2.glClear( GL.GL_COLOR_BUFFER_BIT );	
		}  
	  }
	  
	  
	 /**
	  * GLAction to draw GText 
	  * @author <a href="mailto:Jean-Francois.Lecomte@ifpen.fr">Jef Lecomte</a>
	  *
	  */
	  private class TextGLaction implements GLAction {
		  
		GText text;
		GStyle style;
		public TextGLaction( GText text, GStyle style ) {
			this.text = text;
			this.style = style;
		}  
		
		  public void execute(GL target) {
			  
			  GL2 gl2 = target.getGL2();
			   
			  GColor fg = style.getForegroundColor();
			  
			  GGLFontImpl fontImpl = (GGLFontImpl)style.getFont().getImpl();
			  
			  GLFont glfont = fontImpl.createGLFont(gl2);
		
			  
			  glfont.setColor(
					  (byte)fg.getRed(), 
					  (byte)fg.getGreen(), 
					  (byte)fg.getBlue(),
					  (byte)fg.getAlpha() );
			  
			  glfont.write( gl2, text.getText(), text.getRectangle().x,  text.getRectangle().y );
			
		  }
	  }
	  
	  
	  /**
	   * GLAction to draw Polygonal part of GSegment
	   * @author <a href="mailto:Jean-Francois.Lecomte@ifpen.fr">Jef Lecomte</a>
	   *
	   */
	  private class XYGLaction implements GLAction {
		  
		  int[] x;
		  int[] y;
		  GStyle style;
		  
		  XYGLaction( int x[], int y[], GStyle style ){
			  this.x = x;
			  this.y = y;
			  this.style = style;  
		  }

		  @Override
		  public void execute(GL target) {

			  GL2 gl2 = target.getGL2();
 
			  GColor bg = style.getBackgroundColor();
			  if ( null != bg ){
				  
				  
				  /*  see "Drawing Filled, Concave Polygons Using the Stencil Buffer
				   * in OpenGL redbook. 
				   * http://glprogramming.com/red/chapter14.html#name13
				   * don't forget to set: capabilities_.setStencilBits(8);*/
				  

				  gl2.glEnable( GL.GL_BLEND);
				  gl2.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				  /* Dessin du sprite avec transparence */
				 

				   
				  gl2.glEnable(GL2.GL_STENCIL_TEST);
				  gl2.glClear(GL.GL_STENCIL_BUFFER_BIT);
				  gl2.glColorMask( false, false, false, false);
				  gl2.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL); 
				  gl2.glStencilFunc(GL.GL_NEVER, 0, 1);
				  gl2.glStencilOp(GL.GL_INVERT, GL.GL_INVERT, GL.GL_INVERT);


				  gl2.glBegin( GL2.GL_TRIANGLE_FAN );		
				  for( int i = 1; i<x.length-2; i++ ){
					  gl2.glVertex2i( x[0], y[0] );
					  gl2.glVertex2i( x[i], y[i] );
					  gl2.glVertex2i( x[i+1], y[i+1] );
				  }
				  gl2.glEnd();

				  gl2.glColorMask(true, true, true, true);
				  gl2.glStencilFunc(GL2.GL_NOTEQUAL, 0, 1);
				  gl2.glStencilOp(GL2.GL_KEEP, GL2.GL_ZERO, GL2.GL_ZERO);
				  gl2.glColor4ub((byte)bg.getRed(), (byte)bg.getGreen(), (byte)bg.getBlue(), (byte)bg.getAlpha());
				  gl2.glBegin( GL2.GL_TRIANGLE_FAN );		
				  for( int i = 1; i<x.length-2; i++ ){
					  gl2.glVertex2i( x[0], y[0] );
					  gl2.glVertex2i( x[i], y[i] );
					  gl2.glVertex2i( x[i+1], y[i+1] );
				  }
				  gl2.glEnd();
				  
				  gl2.glDisable(GL2.GL_STENCIL_TEST);
				  gl2.glDisable(GL.GL_BLEND);
				

			  }
			  
			  if ( style.isLineVisible() ){
				  GColor fg = style.getForegroundColor();
				  FloatBuffer widthBuffer = FloatBuffer.allocate(1);
				  gl2.glGetFloatv(GL.GL_LINE_WIDTH, widthBuffer);
				  gl2.glLineWidth( style.getLineWidth() );
				  gl2.glColor3ub((byte)fg.getRed(), (byte)fg.getGreen(), (byte)fg.getBlue());
				  gl2.glBegin( GL2.GL_LINE_STRIP );		//draw polyline
				  for( int i = 0; i<x.length; i++ ){
					  gl2.glVertex2i( x[i], y[i] );
				  }
				  gl2.glEnd();
				  gl2.glLineWidth( widthBuffer.get(0) );
			  }
		  }
		  
	  }
	  
	  /**
		 * map modifiers from AWT event to GEvent modifiers
		 */
		private void setModifiers( MouseEvent event, GEvent gevent ){
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

			GEvent gevent = new GEvent( event.x,  getHeight()- event.y );

			int notches = event.count;
			if (notches > 0) {
				gevent.type = GEvent.WHEEL_MOUSE_UP;
			} else {
				gevent.type = GEvent.WHEEL_MOUSE_DOWN;
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
				window_.mouseMoved( event.x, getHeight()- event.y );
				return;
			}

			int modifiers = event.stateMask;
			GEvent gevent = new GEvent( event.x, getHeight()- event.y );

			if ( ( mouseMask & SWT.BUTTON1 ) == SWT.BUTTON1) {
				gevent.type = GEvent.BUTTON1_DRAG;
			}
			else if (( mouseMask & SWT.BUTTON2 ) == SWT.BUTTON2)  {
				gevent.type = GEvent.BUTTON2_DRAG;
			}
			else
				gevent.type = GEvent.BUTTON3_DRAG;


			setModifiers(event, gevent);
			window_.mouseDragged ( gevent );

		}

		@Override
		public void mouseDown(MouseEvent event) {
			int modifiers = event.stateMask;
			GEvent gevent = new GEvent( event.x, getHeight()- event.y );

			if ( event.button == 1 ) {
				gevent.type = GEvent.BUTTON1_DOWN;
			}
			else if (event.button == 2 ) {
				gevent.type = GEvent.BUTTON2_DOWN;
			}
			else
				gevent.type = GEvent.BUTTON3_DOWN;

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
			GEvent gevent = new GEvent( event.x, getHeight()- event.y );

			if ( event.button == 1 ) {
				gevent.type = GEvent.BUTTON1_UP;
				mouseMask &= ~SWT.BUTTON1;
			}
			else if (event.button == 2 ) {
				gevent.type = GEvent.BUTTON2_UP;
				mouseMask &= ~SWT.BUTTON2;
			}
			else {
				gevent.type = GEvent.BUTTON3_UP;
				mouseMask &= ~SWT.BUTTON3;
			}

			setModifiers(event, gevent);

			window_.mouseReleased( gevent );

		}

		@Override
		public Rect getStringBox(String string, GFont gfont) {
		  
		    GGLFontImpl fontImpl = (GGLFontImpl)gfont.getImpl();
		    return fontImpl.getStringBox(string, gfont);
	       
		}



	

}
