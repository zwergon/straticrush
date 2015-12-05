package no.geosoft.cc.graphics.GL;



import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GComponent;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GKeyEvent;
import no.geosoft.cc.graphics.GMouseEvent;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.interfaces.ICanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLDrawable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;

import fr.ifp.kronosflow.geometry.Rect;
import fr.ifp.kronosflow.geometry.Region;


public class GGLNewtCanvas 
	implements  
		ICanvas,
		GLEventListener,
		MouseListener,
		KeyListener
	{
	
	
	
	private final GWindow    window_;
	
	/** Widget that displays OpenGL content. */
    protected GLWindow glcanvas;
    
    protected NewtCanvasSWT glcomposite;
  
	private Rect             cleared_;
	
	private int mouseMask = 0;
	
	private List<GLAction> queue= new ArrayList<GGLNewtCanvas.GLAction>(16);
	
	private GColor backgroundColor;
	
	public GGLNewtCanvas( Object parent, GWindow window ) {
		
		backgroundColor = GColor.WHITE;
	
		
		GLProfile glprofile = GLProfile.get( GLProfile.GL2 );
		
		GLCapabilities capabilities = new GLCapabilities(glprofile);
		capabilities.setDoubleBuffered(true);
		capabilities.setHardwareAccelerated(true);
		capabilities.setStencilBits(8);
		capabilities.setBackgroundOpaque(false);

		
		glcanvas = GLWindow.create(capabilities);
		glcanvas.setAutoSwapBufferMode(true);
		glcanvas.setUndecorated(true);
		glcomposite = NewtCanvasSWT.create((Composite) parent, SWT.NO_BACKGROUND , glcanvas );
		
		glcanvas.addGLEventListener(this);
		
		glcomposite.setFocus();
		
		//associate to GWindow
		window_ = window;
		
		
		glcanvas.addMouseListener(this);
		glcanvas.addKeyListener(this);
						
	}
	
	
	public GL2 getGL2(){
		return glcanvas.getContext().getGL().getGL2();
	}
	
	public void setBackgroundColor(GColor color) {
		backgroundColor = color;
	};
	
	@Override
	public int getWidth() {
		Rectangle rectangle = glcomposite.getBounds();
		return rectangle.width;
	}



	@Override
	public int getHeight() {
		Rectangle rectangle = glcomposite.getBounds();
		return rectangle.height;
	}
	
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		gl2.setSwapInterval( 0 );
		gl2.glEnable( GL2.GL_SCISSOR_TEST );
		gl2.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );
		gl2.glDisable(GL2.GL_DEPTH_TEST);

	}


	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void display(GLAutoDrawable drawable) {
		execute( drawable );
	}


	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		
		GL2 gl2 = drawable.getGL().getGL2();
		
		
		Rectangle rectangle = glcomposite.getBounds();
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
			glcomposite.redraw(cleared_.x, cleared_.y, cleared_.width, cleared_.height, true );
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
	  
	  private void execute( GLAutoDrawable drawable)
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
			  
			 
			  GL2 gl = drawable.getGL().getGL2();
			  
			  gl.glDrawBuffer( GL.GL_BACK);
			  for ( GLAction action : temp ) {
				  action.execute(gl);
			  } 
			  //glcanvas.swapBuffers();
			  
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
	   * map modifiers from SWT event to GEvent modifiers
	   */
	  private void setModifiers( MouseEvent event, GMouseEvent gevent ){
		  gevent.modifier = GMouseEvent.NONE;
		  if ( ( event.getModifiers() & MouseEvent.ALT_MASK ) == MouseEvent.ALT_MASK ){
			  gevent.setModifier( GMouseEvent.ALT_DOWN_MASK, true );
		  }
		  if ( ( event.getModifiers() & MouseEvent.CTRL_MASK ) == MouseEvent.CTRL_MASK  ){
			  gevent.setModifier( GMouseEvent.CTRL_DOWN_MASK, true );
		  }
		  if ( ( event.getModifiers() & MouseEvent.SHIFT_MASK ) == MouseEvent.SHIFT_MASK ){
			  gevent.setModifier( GMouseEvent.SHIFT_DOWN_MASK, true );
		  }			 
	  }


		@Override
		public Rect getStringBox(String string, GFont gfont) {
		  
		    GGLFontImpl fontImpl = (GGLFontImpl)gfont.getImpl();
		    return fontImpl.getStringBox(string, gfont);
	       
		}

		@Override
		public void keyPressed(KeyEvent event) {
			GKeyEvent ke = new GKeyEvent(
					GKeyEvent.KEY_PRESSED, 
					event.getModifiers(), 
					event.getKeySymbol(), 
					event.getKeyChar(),
					GKeyEvent.KEY_LOCATION_STANDARD );

			window_.keyPressed(ke);
		}

		@Override
		public void keyReleased(KeyEvent event) {
			GKeyEvent ke = new GKeyEvent(GKeyEvent.KEY_RELEASED,
					event.getModifiers(), 
					event.getKeySymbol(), 
					event.getKeyChar(),
					GKeyEvent.KEY_LOCATION_STANDARD );
			window_.keyPressed(ke);
			
		}


		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void mouseEntered(MouseEvent event) {
			 window_.mouseEntered(event.getX(), getHeight() - event.getY());
			
		}


		@Override
		public void mouseExited(MouseEvent event) {
			 window_.mouseExited (event.getX(), getHeight() - event.getY());
			
		}


		@Override
		public void mousePressed(MouseEvent event) {
			 GMouseEvent gevent = new GMouseEvent( event.getX(), getHeight()- event.getY() );

			  if ( event.getButton() == 1 ) {
				  gevent.type = GMouseEvent.BUTTON1_DOWN;
			  }
			  else if (event.getButton() == 2 ) {
				  gevent.type = GMouseEvent.BUTTON2_DOWN;
			  }
			  else
				  gevent.type = GMouseEvent.BUTTON3_DOWN;

			  setModifiers(event, gevent);

			  window_.mousePressed ( gevent );

			  if ( event.getButton() == 1 ) {
				  mouseMask |= SWT.BUTTON1;
			  }
			  else if (event.getButton() == 2 ) {
				  mouseMask |= SWT.BUTTON2;
			  }
			  else
				  mouseMask |= SWT.BUTTON3;

			
		}


		@Override
		public void mouseReleased(MouseEvent event) {
			 GMouseEvent gevent = new GMouseEvent( event.getX(), getHeight()- event.getY() );

			  if ( event.getButton() == 1 ) {
				  gevent.type = GMouseEvent.BUTTON1_UP;
				  mouseMask &= ~SWT.BUTTON1;
			  }
			  else if (event.getButton() == 2 ) {
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


		@Override
		public void mouseMoved(MouseEvent event) {
			 window_.mouseMoved( event.getX(), getHeight()- event.getY() );
			
		}


		@Override
		public void mouseDragged(MouseEvent event) {
		
			GMouseEvent gevent = new GMouseEvent( event.getX(), getHeight()- event.getY() );

			if ( ( mouseMask & SWT.BUTTON1 ) == SWT.BUTTON1) {
				gevent.type = GMouseEvent.BUTTON1_DRAG;
			}
			else if (( mouseMask & SWT.BUTTON2 ) == SWT.BUTTON2)  {
				gevent.type = GMouseEvent.BUTTON2_DRAG;
			}
			else
				gevent.type = GMouseEvent.BUTTON3_DRAG;


			//setModifiers(event, gevent);
			window_.mouseDragged ( gevent );

		}


		@Override
		public void mouseWheelMoved(MouseEvent event) {
			GMouseEvent gevent = new GMouseEvent( event.getX(),  getHeight()- event.getY() );

			  float[] rotation = event.getRotation();
			  //only vertical rotation 
			  if (rotation[1] > 0) {
				  gevent.type = GMouseEvent.WHEEL_MOUSE_UP;
			  } else {
				  gevent.type = GMouseEvent.WHEEL_MOUSE_DOWN;
			  }
			  //setModifiers(event, gevent);
			  window_.wheelMoved( gevent );
			
		}


		

	

}
