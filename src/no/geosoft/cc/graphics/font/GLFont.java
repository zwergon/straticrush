package no.geosoft.cc.graphics.font;

import java.awt.Font;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

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
    

      ByteBuffer tempBuf;
      boolean noImage;
      _charwidth[32] = glyph.setWidth;
      
      for(int index=9; index<280; index++){
          
         Character character = new Character((char)index);
         glyph = font.getGlyph(character);
         if ( null == glyph ){
             continue;
         }
       
        _charmap.put( character, new Integer(index) );
        _charwidth[index]= glyph.setWidth;
        

        noImage = true;
        tempBuf = ByteBuffer.allocate( 4 * glyph.pixels.length );
        for(int t=0; t< glyph.pixels.length; t++){
          tempBuf.putInt(  t*4, (255<<24)+(255<<16)+(255<<8)+(glyph.pixels[t]) );
          if( glyph.pixels[t] >0 ) noImage = false;
        }
        tempBuf.rewind();
        if( noImage ) continue;

        _tex[index] = glNewTex(gl, GL.GL_TEXTURE_2D, _texsize, _texsize, GL.GL_RGBA, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,  GL.GL_NEAREST, GL.GL_NEAREST,  tempBuf);    

        //generate and build the display list
        _list[index] = gl.glGenLists(1);
        gl.glNewList( _list[index] ,GL2.GL_COMPILE);
          gl.glActiveTexture( GL.GL_TEXTURE0); 
          gl.glEnable( GL.GL_TEXTURE_2D ); 
          gl.glBindTexture( GL.GL_TEXTURE_2D, _tex[index] );

          gl.glPushMatrix();
          gl.glBegin(GL.GL_POINTS);
            //i shift the pixel to the char's correct distance from the baseline
            gl.glVertex2f( _texsize/2 + glyph.leftExtent , font.size + _texsize/2 - glyph.topExtent );
          gl.glEnd();
          gl.glPopMatrix();
        gl.glEndList();
      }
      gl.glActiveTexture( GL.GL_TEXTURE0); 
      gl.glEnable( GL.GL_TEXTURE_2D ); 
      gl.glBindTexture( GL.GL_TEXTURE_2D, 0 );
    }

    void deallocate(GL2 gl){
      gl.glDeleteTextures(128, _tex, 0);
      gl.glDeleteLists(128, _list[0] );
    }

    public void setColor(int r, int g, int b, int a){
      _color = new int[]{r,g,b,a};
    }
    
    public void write(GL2 gl, String l_text, float x, float y){
      gl.glPushAttrib( GL2.GL_ALL_ATTRIB_BITS );
      gl.glPushMatrix();
      gl.glColor4ub( (byte)_color[0], (byte)_color[1], (byte)_color[2], (byte)_color[3] );
      gl.glTranslatef(x,y,0);
      gl.glDepthMask(false);
      gl.glDisable(GL.GL_DEPTH_TEST);
      gl.glEnable(GL.GL_BLEND);  //blending is important for alpha

      //set up point sprites
      gl.glEnable(GL2.GL_POINT_SPRITE);
      gl.glPointSize(_texsize);
      gl.glTexEnvi(GL2.GL_POINT_SPRITE, GL2.GL_COORD_REPLACE, GL.GL_TRUE);
     
      for(int i=0; i<l_text.length(); i++){
          if( _charmap.containsKey( l_text.charAt(i)  ) ){
              int t = _charmap.get( l_text.charAt(i)  );
              gl.glCallList( _list[t] );
              gl.glTranslatef( _charwidth[t], 0, 0 );
          }else if( l_text.charAt(i) == (char)32 )
              gl.glTranslatef( _charwidth[32], 0, 0 );
      }
      gl.glPopMatrix();
      gl.glPopAttrib();
    }
  

  //Create a new texture of any type and pass back it's ID
  int glNewTex( GL2 gl, int texType, int w, int h,  int internalFormat, int l_format, int dataType, int minFilter, int magFilter, Buffer data){
    gl.glActiveTexture( GL.GL_TEXTURE0 );
    gl.glEnable(texType);
    int[] temp = new int[1];
    gl.glGenTextures( 1, temp, 0 );

    gl.glBindTexture( texType, temp[0] );
    gl.glTexParameteri( texType, GL.GL_TEXTURE_MIN_FILTER, minFilter );
    gl.glTexParameteri( texType, GL.GL_TEXTURE_MAG_FILTER, magFilter );
    gl.glTexParameteri( texType, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE );
    gl.glTexParameteri( texType, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE );
    gl.glTexImage2D( texType, 0, internalFormat, w, h, 0, l_format, dataType, data );    
    gl.glActiveTexture( GL.GL_TEXTURE0); 
    gl.glEnable( GL.GL_TEXTURE_2D ); 
    gl.glBindTexture( GL.GL_TEXTURE_2D, 0 );
    return temp[0];
  }
  
  }
   
