package no.geosoft.cc.graphics.GL;

/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
 
  This library is free software; you can redistribute it and/or
  modify it under the terms of version 2.01 of the GNU Lesser General
  Public License as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/


import java.awt.*;

import javax.media.opengl.GL2;

import fr.ifp.kronosflow.geometry.Rect;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.interfaces.IFontImpl;



public class GGLFontImpl implements IFontImpl {

 

  /**
   * Name of the font as seen by Java when it was created.
   * If the font is available, the native version will be used.
   */
  protected String name;


  /**
   * The original size of the font when it was first created
   */
  protected int size;

  /** true if smoothing was enabled for this font, used for native impl */
  protected boolean smooth;


  
  /**
   * Native Java version of the font. If possible, this allows the
   * PGraphics subclass to just use Java's font rendering stuff
   * in situations where that's faster.
   */
  protected GFont font;

 
  /**
   * Create a new image-based font on the fly. If charset is set to null,
   * the characters will only be created as bitmaps when they're drawn.
   *
   * @nowebref
   * @param charset array of all unicode chars that should be included
   */
  public GGLFontImpl( GFont font, boolean smooth ) {
    // save this so that we can use the native version
    this.font = font;
    this.smooth = smooth;

    name = font.getFontName();
    size = font.getSize();
    
  }

  public String getName() {
    return name;
  }

 
  /**
   * Return size of this font.
   */
  public int getSize() {
    return size;
  }

 
 
  public Rect getStringBox (String string, GFont gfont)
  {
	  if ( glfont == null ){
          glfont = new GLFont(this);
      }
	  return glfont.getStringBox(string, gfont);
  }
  
  
  public GLFont createGLFont( GL2 gl2 ) {
      if ( glfont == null ){
          glfont = new GLFont(this);
      }
      
      if ( !glfont.hasGLObjects() )
    	  glfont.createGLObjects(gl2);
   
      return glfont;
  }
  
  private static GLFont glfont = null;
}

