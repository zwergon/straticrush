package no.geosoft.cc.graphics.GL;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import no.geosoft.cc.graphics.GL.GGLFontImpl.Glyph;

public class GLFont{
  
    int base;
    int _color[];
    IntBuffer textures;
    
    static final int NCHARACTERS = 256; /* only handle first 280 characters */
    
    public GLFont( GL2 gl, GGLFontImpl font ){
        
     
   
      
        gl.glEnable(GL2.GL_TEXTURE_2D);
        
        int base = gl.glGenLists(NCHARACTERS);
        
        textures = IntBuffer.allocate(NCHARACTERS);
        gl.glGenTextures(NCHARACTERS, textures);
       
        
        for(int index=0; index<NCHARACTERS; index++){
            
           Character character = new Character((char)index);
           Glyph glyph = font.getGlyph(character);
           if ( null == glyph ){
               glyph = font.getGlyph((char)32);
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

    void deallocate(GL2 gl){
      gl.glDeleteTextures(NCHARACTERS, textures );
      gl.glDeleteLists(base, NCHARACTERS );
    }

    public void setColor(int r, int g, int b, int a){
      _color = new int[]{r,g,b,a};
    }
    
    public void write(GL2 gl, String l_text, float x, float y){
        gl.glPushMatrix();
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_BLEND);  //blending is important for alpha
        
        gl.glColor4ub( (byte)_color[0], (byte)_color[1], (byte)_color[2], (byte)_color[3] );
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
       
        gl.glTranslatef(x,y,0);
        for(int i=0; i<l_text.length(); i++){
        	int t = l_text.charAt(i);
        	gl.glCallList( base + t + 1);
        }
        
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_BLEND);  //blending is important for alpha
        gl.glPopMatrix();
     
    }
  
    
    int createTexture(GL2 gl, Glyph glyph, int tex_id ){
        
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


  
  }
   
