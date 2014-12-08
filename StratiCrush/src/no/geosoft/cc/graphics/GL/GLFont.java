package no.geosoft.cc.graphics.GL;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import no.geosoft.cc.graphics.GFont;
import fr.ifp.kronosflow.geometry.Rect;

public class GLFont{
	
	GGLFontImpl fontImpl;
  
    int base = -1;
    int _color[];
    IntBuffer textures;
    
    /** Number of character glyphs in this font. */
    protected int glyphCount;

    /**
     * Actual glyph data. The length of this array won't necessarily be the
     * same size as glyphCount, in cases where lazy font loading is in use.
     */
    protected Glyph[] glyphs;
    
    protected HashMap<Character, Integer> charToIndex = new HashMap<Character, Integer>();
    
    /**
     * The ascent of the font. If the 'd' character is present in this PFont,
     * this value is replaced with its pixel height, because the values returned
     * by FontMetrics.getAscent() seem to be terrible.
     */
    protected int ascent;

    /**
     * The descent of the font. If the 'p' character is present in this PFont,
     * this value is replaced with its lowest pixel height, because the values
     * returned by FontMetrics.getDescent() are gross.
     */
    protected int descent;

    
    //  /**
    //  * If not null, this font is set to load dynamically. This is the default
    //  * when createFont() method is called without a character set. Bitmap
    //  * versions of characters are only created when prompted by an index() call.
    //  */
    // protected Font lazyFont;
    protected Font font;
    protected BufferedImage lazyImage;
    protected Graphics2D lazyGraphics;
    protected FontMetrics lazyMetrics;
    protected int[] lazySamples;
    
    static final int NCHARACTERS = 256; /* only handle first 280 characters */
    
    public GLFont( GGLFontImpl fontImpl ){
    	
    	this.fontImpl = fontImpl;
    	this.font = new Font(fontImpl.getName(),Font.BOLD, fontImpl.getSize() ); 
    	
    	init();
    }
    
    public boolean hasGLObjects(){
    	return base != -1;
    }
    
    
    /**
     * creates all textured GLList associated to all {@link Glyph}.
     * @param gl
     */
    public void createGLObjects( GL2 gl){
           
        gl.glEnable(GL2.GL_TEXTURE_2D);
        
        base = gl.glGenLists(NCHARACTERS);
        
        textures = IntBuffer.allocate(NCHARACTERS);
        gl.glGenTextures(NCHARACTERS, textures);
       
        
        for(int index=0; index<NCHARACTERS; index++){
            
           Character character = new Character((char)index);
           Glyph glyph = getGlyph(character);
           if ( null == glyph ){
               glyph = getGlyph((char)32);
           }
           
 
          //generate and build the display list
          gl.glNewList(base+index, GL2.GL_COMPILE );
          {
        	  
        	  createTexture(gl, glyph, textures.get(index));
        	  
        	  int x = glyph.leftExtent ;
        	  int y = glyph.topExtent;
        	  gl.glBegin(GL2.GL_QUADS);
        	  gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2i( x, y );
        	  gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2i( x + glyph.width, y );
        	  gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2i( x + glyph.width, y - glyph.height);
        	  gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2i( x, y - glyph.height );
        	  gl.glEnd();
        	  gl.glTranslatef( glyph.setWidth, 0, 0 );

          }
          gl.glEndList();
        }
     
    }
    
    /**
     * initialize data for computations of Glyph using awt Font framework.
     */
    public void init(){
    	
    	glyphs = new Glyph[NCHARACTERS];
    	

    	int mbox3 = fontImpl.size * 3;

    	lazyImage = new BufferedImage(mbox3, mbox3, BufferedImage.TYPE_INT_RGB);
    	lazyGraphics = (Graphics2D) lazyImage.getGraphics();
    	lazyGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    			fontImpl.smooth ?
    					RenderingHints.VALUE_ANTIALIAS_ON :
    						RenderingHints.VALUE_ANTIALIAS_OFF);
    	// adding this for post-1.0.9
    	lazyGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
    			fontImpl.smooth ?
    					RenderingHints.VALUE_TEXT_ANTIALIAS_ON :
    						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

    	lazyGraphics.setFont(font);
    	lazyMetrics = lazyGraphics.getFontMetrics();
    	lazySamples = new int[mbox3 * mbox3];

    	// If not already created, just create these two characters to calculate
    	// the ascent and descent values for the font. This was tested to only
    	// require 5-10 ms on a 2.4 GHz MacBook Pro.
    	// In versions 1.0.9 and earlier, fonts that could not display d or p
    	// used the max up/down values as calculated by looking through the font.
    	// That's no longer valid with the auto-generating fonts, so we'll just
    	// use getAscent() and getDescent() in such (minor) cases.
    	if (ascent == 0) {
    		if (font.canDisplay('d')) {
    			new Glyph('d');
    		} else {
    			ascent = lazyMetrics.getAscent();
    		}
    	}
    	if (descent == 0) {
    		if (font.canDisplay('p')) {
    			new Glyph('p');
    		} else {
    			descent = lazyMetrics.getDescent();
    		}
    	}
    }
    
    /**
     * return the {@link Rect} corresponding to bounding box of 
     * the string passed as argument.
     * @param string the string to compute the Bounding Box.
     * @param gfont unused
     * @return
     */
    public Rect getStringBox (String string, GFont gfont)
    {
    	TextLayout textLayout = new TextLayout (string, font, 
    			lazyGraphics.getFontRenderContext());
    	Rectangle2D bounds = textLayout.getBounds();



    	int width  = (int) Math.ceil (bounds.getWidth());
    	int height = (int) Math.ceil (bounds.getHeight());

    	return new Rect (0, 0, width, height);
    }
    

    void deallocate(GL2 gl){
      gl.glDeleteTextures(NCHARACTERS, textures );
      gl.glDeleteLists(base, NCHARACTERS );
    }

    /**
     * 
     */
    public void setColor(int r, int g, int b, int a){
      _color = new int[]{r,g,b,a};
    }
    
    /**
     * writes string l_text at position x,y in gl context.
     * @param gl
     * @param l_text
     * @param x
     * @param y
     */
    public void write( GL2 gl, String l_text, float x, float y){
        gl.glPushMatrix();
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);  //blending is important for alpha
        
        gl.glColor4ub( (byte)_color[0], (byte)_color[1], (byte)_color[2], (byte)_color[3] );
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
       
        gl.glTranslatef(x,y,0);
        for(int i=0; i<l_text.length(); i++){
        	int t = l_text.charAt(i);
        	gl.glCallList( base + t );
        }
        
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_BLEND);  //blending is important for alpha
        gl.glPopMatrix();
     
    }
  
    
    private int createTexture(GL2 gl, Glyph glyph, int tex_id ){
        
        int w = glyph.width;
        int h = glyph.height;
        
        ByteBuffer brga = ByteBuffer.allocate(w*h*4);
        byte[] pbrga = brga.array();
        
        for( int i = 0; i<w*h; i++ ){
            pbrga[4*i] = (byte) 255;
            pbrga[4*i+1]= (byte) 255;
            pbrga[4*i+2]= (byte) 255;
            pbrga[4*i+3]= (byte)glyph.pixels[i];
        }
        
        
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tex_id );
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
       
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA, w, h, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, brga);
        
        
        return tex_id;
    }
    
    
    //////////////////////////////////////////////////////////////
    

    /**
     * Create a new glyph, and add the character to the current font.
     * @param c character to create an image for.
     */
    protected void addGlyph(char c) {
        Glyph glyph = new Glyph(c);
        
        glyph.index = glyphCount;
        charToIndex.put( c,  glyph.index );
        glyphs[ glyph.index] = glyph;
        glyphCount++;
    }
    
    public Glyph getGlyph(char c) {
    	int index = index(c);
    	return (index == -1) ? null : glyphs[index];
    }
    

    /**
     * Get index for the character.
     * @return index into arrays or -1 if not found
     */
    protected int index(char c) {
    	
    
        int index = indexActual(c);
        if (index != -1) {
          return index;
        }
        if (font != null && font.canDisplay(c)) {
          // create the glyph
          addGlyph(c);
          // now where did i put that?
          return indexActual(c);

        } 
        return -1;
    }


    protected int indexActual(char c) {
      // degenerate case, but the find function will have trouble
      // if there are somehow zero chars in the lookup
      //if (value.length == 0) return -1;
      if (glyphCount == 0) return -1;
      
      if ( charToIndex.containsKey(c) ){
          return charToIndex.get(c);
      }
      
      return -1;
    }




    /**
     * Returns the ascent of this font from the baseline.
     * The value is based on a font of size 1.
     */
    public float ascent() {
      return ((float) ascent / (float) fontImpl.size);
    }


    /**
     * Returns how far this font descends from the baseline.
     * The value is based on a font size of 1.
     */
    public float descent() {
      return ((float) descent / (float) fontImpl.size);
    }


    /**
     * Width of this character for a font of size 1.
     */
    public float width(char c) {
      if (c == 32) return width('i');

      int cc = index(c);
      if (cc == -1) return 0;

      return ((float) glyphs[cc].setWidth / (float) fontImpl.size);
    }




    public int getGlyphCount()  {
      return glyphCount;
    }


    public Glyph getGlyph(int i)  {
      return glyphs[i];
    }



    /**
     * Grayscale bitmap font class used by Processing.
     * <P>
     * Awful (and by that, I mean awesome) ASCII (non-)art for how this works:
     * <PRE>
     *   |
     *   |                   height is the full used height of the image
     *   |
     *   |   ..XX..       }
     *   |   ..XX..       }
     *   |   ......       }
     *   |   XXXX..       }  topExtent (top y is baseline - topExtent)
     *   |   ..XX..       }
     *   |   ..XX..       }  dotted areas are where the image data
     *   |   ..XX..       }  is actually located for the character
     *   +---XXXXXX----   }  (it extends to the right and down
     *   |                   for power of two texture sizes)
     *   ^^^^ leftExtent (amount to move over before drawing the image
     *
     *   ^^^^^^^^^^^^^^ setWidth (width displaced by char)
     */



    /**
     * A single character, and its visage.
     */
    public class Glyph {
      public int[] pixels;
      public int value;
      public int height;
      public int width;
      public int index;
      public int setWidth;
      public int topExtent;
      public int leftExtent;


      public Glyph() {
        index = -1;
        // used when reading from a stream or for subclasses
      }

      protected Glyph(char c) {
    	  
    	  
        int mbox3 = fontImpl.size * 3;
        lazyGraphics.setColor(Color.white);
        lazyGraphics.fillRect(0, 0, mbox3, mbox3);
        lazyGraphics.setColor(Color.black);
        lazyGraphics.drawString(String.valueOf(c), fontImpl.size, fontImpl.size * 2);

        WritableRaster raster = lazyImage.getRaster();
        raster.getDataElements(0, 0, mbox3, mbox3, lazySamples);

        int minX = 1000, maxX = 0;
        int minY = 1000, maxY = 0;
        boolean pixelFound = false;

        for (int y = 0; y < mbox3; y++) {
          for (int x = 0; x < mbox3; x++) {
            int sample = lazySamples[y * mbox3 + x] & 0xff;
            if (sample != 255) {
              if (x < minX) minX = x;
              if (y < minY) minY = y;
              if (x > maxX) maxX = x;
              if (y > maxY) maxY = y;
              pixelFound = true;
            }
          }
        }

        if (!pixelFound) {
          minX = minY = 0;
          maxX = maxY = 0;
          // this will create a 1 pixel white (clear) character..
          // maybe better to set one to -1 so nothing is added?
        }

        value = c;
        height = (maxY - minY) + 1;
        width = (maxX - minX) + 1;
        setWidth = lazyMetrics.charWidth(c);

        // offset from vertical location of baseline
        // of where the char was drawn (size*2)
        topExtent = fontImpl.size*2 - minY;

        // offset from left of where coord was drawn
        leftExtent = minX - fontImpl.size;

        
        pixels = new int[width*height];
        for (int y = minY; y <= maxY; y++) {
          for (int x = minX; x <= maxX; x++) {
            int val = 255 - (lazySamples[y * mbox3 + x] & 0xff);
            int pindex = (y - minY) * width + (x - minX);
            pixels[pindex] = val;
          }
        }

        // replace the ascent/descent values with something.. err, decent.
        if (value == 'd') {
          if (ascent == 0) ascent = topExtent;
        }
        if (value == 'p') {
          if (descent == 0) descent = -topExtent + height;
        }
      }
    }
    


  
  }
   
