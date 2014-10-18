package no.geosoft.cc.graphics.font;

import java.awt.Font;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import fr.ifp.kronosflow.geometry.Rect;
import no.geosoft.cc.graphics.font.GGLFontImpl.Glyph;

public class GLFont{
    int _texsize, _tex[], _list[];
    float _charwidth[];
    int _color[];
   
    Hashtable<Character, Integer> _charmap;
    
    public GLFont( GL2 gl, GGLFontImpl font ){
     
        Glyph glyph = font.getGlyph((char)32);
        if ( null == glyph ){
            return;
        }
        
   
        _texsize = glyph.width;
        _tex = new int[280];
        _list = new int[280]; 
        _charwidth = new float[280];
        _charmap = new Hashtable<Character,Integer>();
      

        _charwidth[32] = glyph.setWidth;
        
        for(int index=9; index<280; index++){
            
           Character character = new Character((char)index);
           glyph = font.getGlyph(character);
           if ( null == glyph ){
               continue;
           }
         
          _charmap.put( character, new Integer(index) );
          _charwidth[index]= glyph.setWidth;
          _tex[index] = createTexture(gl, glyph);
          
          //generate and build the display list
          _list[index] =  gl.glGenLists(1);
          gl.glNewList(_list[index], GL2.GL_COMPILE );

          gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
          gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
          gl.glEnable(GL2.GL_TEXTURE_2D);
          
      
          int x = glyph.leftExtent ;
          int y = glyph.topExtent;
          gl.glBegin(GL2.GL_QUADS);
          gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex2i( x, y );
          gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex2i( x + glyph.width, y );
          gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex2i( x + glyph.width, y - glyph.height);
          gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex2i( x, y - glyph.height );
          gl.glEnd();
          
          
          gl.glDisable(GL2.GL_TEXTURE_2D);

          gl.glEndList();
        }
     
    }

    void deallocate(GL2 gl){
      gl.glDeleteTextures(128, _tex, 0);
      gl.glDeleteLists(128, _list[0] );
    }

    public void setColor(int r, int g, int b, int a){
      _color = new int[]{r,g,b,a};
    }
    
    public void write(GL2 gl, String l_text, float x, float y){
        gl.glPushMatrix();
        gl.glEnable(GL.GL_BLEND);  //blending is important for alpha
        gl.glColor4ub( (byte)_color[0], (byte)_color[1], (byte)_color[2], (byte)_color[3] );
        gl.glTranslatef(x,y,0);
        
        for(int i=0; i<l_text.length(); i++){
            
            int t = _charmap.get( (char)32  );
            if( _charmap.containsKey( l_text.charAt(i)  ) ){
                t = _charmap.get( l_text.charAt(i)  );
            }
            gl.glBindTexture(GL.GL_TEXTURE_2D, _tex[t]);
            gl.glCallList( _list[t] );
            gl.glTranslatef( _charwidth[t], 0, 0 );

        }
        gl.glDisable(GL.GL_BLEND);  //blending is important for alpha
        gl.glPopMatrix();
     
    }
  
    
    int createTexture(GL2 gl, Glyph glyph ){
        
        int w = glyph.width;
        int h = glyph.height;
        //int s = Math.max( w, h);
        
        ByteBuffer brga = ByteBuffer.allocate(w*h*4);
        byte[] pbrga = brga.array();
        
        for( int i = 0; i<w*h; i++ ){
            pbrga[4*i] = (byte) 255;
            pbrga[4*i+1]= (byte) 255;
            pbrga[4*i+2]= (byte) 255;
            pbrga[4*i+3]= (byte)glyph.pixels[i];
        }
        
        
        IntBuffer texture = IntBuffer.allocate(1);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glGenTextures(1, texture);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.get(0));
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
       
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL.GL_RGBA, w, h, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, brga);
        
        
        return texture.get(0);
    }


  
  }
   
