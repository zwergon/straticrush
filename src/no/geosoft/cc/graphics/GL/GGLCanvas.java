package no.geosoft.cc.graphics.GL;


import java.awt.Color;
import java.awt.Frame;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
 
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import jogamp.opengl.gl4.GL4bcImpl;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GComponent;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.interfaces.ICanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.jogamp.opengl.util.gl2.GLUT;

import fr.ifp.kronosflow.geometry.Rect;
import fr.ifp.kronosflow.geometry.Region;


@SuppressWarnings("serial")
public class GGLCanvas extends GLCanvas 
	implements  
			GLEventListener, 
			ICanvas,
			MouseListener,
			MouseMotionListener,
			MouseWheelListener
	{
	
	static private GLCapabilities capabilities_;
	
	private final GWindow    window_;
	private Frame            awt_frame_;
	private Rect             cleared_;
	private GLUT             glut_;
	
	private List<GLAction> queue= new ArrayList<GGLCanvas.GLAction>(16);
	
	static {
		GLProfile gprofile = GLProfile.getDefault();
		capabilities_ = new GLCapabilities(gprofile);
		capabilities_.setDoubleBuffered(true);
		capabilities_.setHardwareAccelerated(true);
		capabilities_.setStencilBits(8);
	}


	public GGLCanvas( Object parent, GWindow window ) {
		
		super( capabilities_ );

		// we can't use the default Composite because using the AWT bridge
		// requires that it have the property of SWT.EMBEDDED
		Composite compo = new Composite((Composite)parent, SWT.EMBEDDED );
		compo.setLayout(new FillLayout());
		
		// create the special frame bridge to AWT
		awt_frame_ = SWT_AWT.new_Frame(compo);
		
		
		//associate to GWindow
		window_ = window;
		
		//System.setProperty("sun.awt.noerasebackground", "true");
		
		// we need the listener so we get the GL events
		addGLEventListener(this);

		// finally, add our canvas as a child of the frame
		awt_frame_.add(this);
		
		glut_ = new GLUT();
		
		addMouseListener (this);
		addMouseMotionListener (this);
		addMouseWheelListener(this);

	}


	@Override
	public void display(GLAutoDrawable drawable) {
		execute(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		

		GL2 gl2 = drawable.getGL().getGL2();

		gl2.glDisable(GL.GL_DEPTH_TEST);

		gl2.glDrawBuffer( GL.GL_BACK );
		gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
				
	}

	@Override
	public void reshape(
			GLAutoDrawable drawable, 
			int x, int y, 
			int width, int height) {
		

		GL2 gl2 = drawable.getGL().getGL2();
		
		// set the swap interval to as fast as possible
		gl2.setSwapInterval(0);
		
		gl2.glMatrixMode( GL2.GL_PROJECTION );
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as the window
		GLU glu = new GLU();
		glu.gluOrtho2D( 0.0f, width, 0.0f, height );
		
		gl2.glMatrixMode( GL2.GL_MODELVIEW );
		gl2.glLoadIdentity();
		
		//

		cleared_ = new Rect (0, 0, width, height);
		
		window_.resize();

	}
	
	/**
	 * Refresh this canvas.
	 */
	public void initRefresh()
	{
		//do nothing
	}

	/**
	 * Refresh this canvas.
	 */
	public void refresh()
	{
		if ( null != cleared_ ){
			repaint(cleared_.x, cleared_.y, cleared_.width, cleared_.height );
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
	  public void render ( GSegment segment, GStyle style )
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
	  
	  private void execute(GLAutoDrawable drawable)
	  {
		  // make a copy of the queue to allow thread safe iteration
		  ArrayList<GLAction> temp = null;
		 
		  
		  synchronized (queue)
		  {
			  // Only make a copy, if the queue has entries
			  if( queue.size() != 0 )
			  {
				  temp = new ArrayList<GLAction>(queue); 
				  queue.clear();
			  }
		  }

		  // iterate outside of the synchronization to avoid blocking the queue
		  if( temp!=null ){
			  drawable.getContext().makeCurrent();
			  drawable.setAutoSwapBufferMode(false);
			  
			  GL2 gl2 = drawable.getGL().getGL2();
			  gl2.glDrawBuffer( GL.GL_BACK );
			  
			  gl2.glEnable( GL.GL_SCISSOR_TEST );
			  for ( GLAction action : temp ) {
				  action.execute(drawable.getGL());
			  }
			  gl2.glDisable( GL.GL_SCISSOR_TEST );
			 
		  
			  drawable.swapBuffers();
			  drawable.getContext().release();
			  
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
			gl2.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
			  gl2.glColor3ub((byte)fg.getRed(), (byte)fg.getGreen(), (byte)fg.getBlue());
			  
			  gl2.glRasterPos2i(text.getRectangle().x,  text.getRectangle().y);
			  
			  GGLBitmapFont gl_font = (GGLBitmapFont)style.getFont().getImpl();
			  glut_.glutBitmapString( gl_font.getType(), text.getText() );
			  
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
				  gl2.glColor3ub((byte)bg.getRed(), (byte)bg.getGreen(), (byte)bg.getBlue());
				  gl2.glBegin( GL2.GL_TRIANGLE_FAN );		
				  for( int i = 1; i<x.length-2; i++ ){
					  gl2.glVertex2i( x[0], y[0] );
					  gl2.glVertex2i( x[i], y[i] );
					  gl2.glVertex2i( x[i+1], y[i+1] );
				  }
				  gl2.glEnd();
				  
				  gl2.glDisable(GL2.GL_STENCIL_TEST);
				

			  }
			  
			  if ( style.isLineVisible() ){
				  GColor fg = style.getForegroundColor();
				  gl2.glColor3ub((byte)fg.getRed(), (byte)fg.getGreen(), (byte)fg.getBlue());
				  gl2.glBegin( GL2.GL_LINE_STRIP );		//draw polyline
				  for( int i = 0; i<x.length; i++ ){
					  gl2.glVertex2i( x[i], y[i] );
				  }
				  gl2.glEnd();
			  }
		  }
		  
	  }

	  
	  /**
	   * map modifiers from AWT event to GEvent modifiers
	   */
	  private void setModifiers( MouseEvent event, GEvent gevent ){
		  gevent.modifier = GEvent.NONE;
		  if ( event.isAltDown() ){
			  gevent.setModifier( GEvent.ALT_DOWN_MASK, true );
		  }
		  if ( event.isControlDown() ){
			  gevent.setModifier( GEvent.CTRL_DOWN_MASK, true );
		  }
		  if ( event.isMetaDown() ){
			  gevent.setModifier( GEvent.META_DOWN_MASK, true );
		  }
	  }

	  
	  /**
	   * Method called when a mouse pressed event occurs in this window.
	   * If an interaction is installed, pass a BUTTON*_DOWN event to it.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mousePressed (MouseEvent event)
	  {

		 
		  int modifiers = event.getModifiers();
		  GEvent gevent = new GEvent( event.getX(), getHeight() - event.getY() );

		  if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
			  gevent.type = GEvent.BUTTON1_DOWN;
		  }
		  else if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
			  gevent.type = GEvent.BUTTON2_DOWN;
		  }
		  else{
			  
		  }
			 

		  setModifiers(event, gevent);

		  window_.mousePressed ( gevent );
	  }


	  
	  /**
	   * Method called when a mouse release event occurs in this window.
	   * If an interaction is installed, pass a BUTTON*_UP event to it.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mouseReleased (MouseEvent event)
	  {
		  int modifiers = event.getModifiers();
		  GEvent gevent = new GEvent( event.getX(), getHeight() - event.getY() );

		  if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
			  gevent.type = GEvent.BUTTON1_UP;
		  }
		  else if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
			  gevent.type = GEvent.BUTTON2_UP;
		  }
		  else {
			  gevent.type = GEvent.BUTTON3_UP;
		  }
		  setModifiers(event, gevent);
		  window_.mouseReleased ( gevent );
	  }

	  

	  /**
	   * Method called when the mouse is dragged (moved with button pressed) in
	   * this window. If an interaction is installed, pass a BUTTON*_DRAG
	   * event to it.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mouseDragged (MouseEvent event)
	  {
		  int modifiers = event.getModifiers();
		  GEvent gevent = new GEvent( event.getX(), getHeight() - event.getY() );

		  if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
			  gevent.type = GEvent.BUTTON1_DRAG;
		  }
		  else if ((modifiers & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) {
			  gevent.type = GEvent.BUTTON2_DRAG;
		  }
		  else {
			  gevent.type = GEvent.BUTTON3_DRAG;
		  }
		  setModifiers(event, gevent);
		  window_.mouseDragged ( gevent );
	  }
	  
	  
	 
	  public void mouseWheelMoved(MouseWheelEvent event ) {

		  GEvent gevent = new GEvent( event.getX(), getHeight() - event.getY() );

		  int notches = event.getWheelRotation();
		  if (notches < 0) {
			  gevent.type = GEvent.WHEEL_MOUSE_UP;
		  } else {
			  gevent.type = GEvent.WHEEL_MOUSE_DOWN;
		  }
		  setModifiers(event, gevent);
		  window_.wheelMoved( gevent );

	  }


	  
	  /**
	   * Method called when the mouse is moved inside this window.
	   * If an interaction is installed, pass a MOTION event to it.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mouseMoved (MouseEvent event)
	  {
	    window_.mouseMoved (event.getX(), getHeight() - event.getY());
	  }


	  /**
	   * Implied by MouseListener. Not used.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mouseClicked (MouseEvent event)
	  {
	  }


	  
	  /**
	   * Method called when the pointer enters this window. If an interaction
	   * is installed, pass a FOCUS_IN event to it.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mouseEntered (MouseEvent event)
	  {
	    window_.mouseEntered (event.getX(), getHeight() - event.getY());
	  }


	  
	  /**
	   * Method called when the pointer exits this window. If an interaction
	   * is installed, pass a FOCUS_OUT event to it.
	   * 
	   * @param event  Mouse event trigging this method.
	   */
	  public void mouseExited (MouseEvent event)
	  {
	    window_.mouseExited (event.getX(), getHeight() - event.getY());
	  }
	  
	  /**
	   * Utility method for computing the rectangle bounding box of
	   * a rendered string using the specified font.
	   * 
	   * @param string  Sample string.
	   * @param font    Font to use.
	   * @return        Rectangle bounding box of rendered string.
	   */
	  public Rect getStringBox (String string, GFont font)
	  {
		  GGLBitmapFont gl_font = (GGLBitmapFont)font.getImpl();
		  return new Rect (0, 0, 
				  glut_.glutBitmapLength(gl_font.getType(), string), 
				  font.getSize() );
	  }


}
