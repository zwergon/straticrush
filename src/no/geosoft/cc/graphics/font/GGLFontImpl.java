package no.geosoft.cc.graphics.font;

/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2004-10 Ben Fry & Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

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
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

import javax.media.opengl.GL2;

import fr.ifp.kronosflow.geometry.Rect;
import no.geosoft.cc.graphics.GFont;
import no.geosoft.cc.interfaces.IFontImpl;


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
 * </PRE>
 * @webref typography
 * @see PApplet#loadFont(String)
 * @see PApplet#createFont(String, float, boolean, char[])
 * @see PGraphics#textFont(PFont)
 */
public class GGLFontImpl implements IFontImpl {

  /** Number of character glyphs in this font. */
  protected int glyphCount;

  /**
   * Actual glyph data. The length of this array won't necessarily be the
   * same size as glyphCount, in cases where lazy font loading is in use.
   */
  protected Glyph[] glyphs;
  
  protected HashMap<Character, Integer> charToIndex = new HashMap<Character, Integer>();

  /**
   * Name of the font as seen by Java when it was created.
   * If the font is available, the native version will be used.
   */
  protected String name;

  /**
   * Postscript name of the font that this bitmap was created from.
   */
  protected String psname;

  /**
   * The original size of the font when it was first created
   */
  protected int size;

  /** true if smoothing was enabled for this font, used for native impl */
  protected boolean smooth;

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

  
  /**
   * Native Java version of the font. If possible, this allows the
   * PGraphics subclass to just use Java's font rendering stuff
   * in situations where that's faster.
   */
  protected Font font;

  
  /**
   * Array of the native system fonts. Used to lookup native fonts by their
   * PostScript name. This is a workaround for a several year old Apple Java
   * bug that they can't be bothered to fix.
   */
  static protected Font[] fonts;

//  /**
//   * If not null, this font is set to load dynamically. This is the default
//   * when createFont() method is called without a character set. Bitmap
//   * versions of characters are only created when prompted by an index() call.
//   */
//  protected Font lazyFont;
  protected BufferedImage lazyImage;
  protected Graphics2D lazyGraphics;
  protected FontMetrics lazyMetrics;
  protected int[] lazySamples;


  /** for subclasses that need to store metadata about the font */
//  protected HashMap<PGraphics, Object> cacheMap;

  /**
   * @nowebref
   */
  public GGLFontImpl() { }  // for subclasses


 


  /**
   * Create a new image-based font on the fly. If charset is set to null,
   * the characters will only be created as bitmaps when they're drawn.
   *
   * @nowebref
   * @param charset array of all unicode chars that should be included
   */
  public GGLFontImpl(Font font, boolean smooth) {
    // save this so that we can use the native version
    this.font = font;
    this.smooth = smooth;

    name = font.getName();
    psname = font.getPSName();
    size = font.getSize();
    
    glyphs = new Glyph[300];

    int mbox3 = size * 3;

    lazyImage = new BufferedImage(mbox3, mbox3, BufferedImage.TYPE_INT_RGB);
    lazyGraphics = (Graphics2D) lazyImage.getGraphics();
    lazyGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                  smooth ?
                                  RenderingHints.VALUE_ANTIALIAS_ON :
                                  RenderingHints.VALUE_ANTIALIAS_OFF);
    // adding this for post-1.0.9
    lazyGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                  smooth ?
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


  public String getName() {
    return name;
  }


  public String getPostScriptName() {
    return psname;
  }


  /**
   * Set the native complement of this font. Might be set internally via the
   * findFont() function, or externally by a deriveFont() call if the font
   * is resized by PGraphicsJava2D.
   */
  public void setNative(Object font) {
    this.font = (Font) font;
  }


  public Font getFont() {
    return font;
  }

  /**
   * Return size of this font.
   */
  public int getSize() {
    return size;
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
    return ((float) ascent / (float) size);
  }


  /**
   * Returns how far this font descends from the baseline.
   * The value is based on a font size of 1.
   */
  public float descent() {
    return ((float) descent / (float) size);
  }


  /**
   * Width of this character for a font of size 1.
   */
  public float width(char c) {
    if (c == 32) return width('i');

    int cc = index(c);
    if (cc == -1) return 0;

    return ((float) glyphs[cc].setWidth / (float) size);
  }


  //////////////////////////////////////////////////////////////


  public int getGlyphCount()  {
    return glyphCount;
  }


  public Glyph getGlyph(int i)  {
    return glyphs[i];
  }


  //////////////////////////////////////////////////////////////


  static final char[] EXTRA_CHARS = {
    0x0080, 0x0081, 0x0082, 0x0083, 0x0084, 0x0085, 0x0086, 0x0087,
    0x0088, 0x0089, 0x008A, 0x008B, 0x008C, 0x008D, 0x008E, 0x008F,
    0x0090, 0x0091, 0x0092, 0x0093, 0x0094, 0x0095, 0x0096, 0x0097,
    0x0098, 0x0099, 0x009A, 0x009B, 0x009C, 0x009D, 0x009E, 0x009F,
    0x00A0, 0x00A1, 0x00A2, 0x00A3, 0x00A4, 0x00A5, 0x00A6, 0x00A7,
    0x00A8, 0x00A9, 0x00AA, 0x00AB, 0x00AC, 0x00AD, 0x00AE, 0x00AF,
    0x00B0, 0x00B1, 0x00B4, 0x00B5, 0x00B6, 0x00B7, 0x00B8, 0x00BA,
    0x00BB, 0x00BF, 0x00C0, 0x00C1, 0x00C2, 0x00C3, 0x00C4, 0x00C5,
    0x00C6, 0x00C7, 0x00C8, 0x00C9, 0x00CA, 0x00CB, 0x00CC, 0x00CD,
    0x00CE, 0x00CF, 0x00D1, 0x00D2, 0x00D3, 0x00D4, 0x00D5, 0x00D6,
    0x00D7, 0x00D8, 0x00D9, 0x00DA, 0x00DB, 0x00DC, 0x00DD, 0x00DF,
    0x00E0, 0x00E1, 0x00E2, 0x00E3, 0x00E4, 0x00E5, 0x00E6, 0x00E7,
    0x00E8, 0x00E9, 0x00EA, 0x00EB, 0x00EC, 0x00ED, 0x00EE, 0x00EF,
    0x00F1, 0x00F2, 0x00F3, 0x00F4, 0x00F5, 0x00F6, 0x00F7, 0x00F8,
    0x00F9, 0x00FA, 0x00FB, 0x00FC, 0x00FD, 0x00FF, 0x0102, 0x0103,
    0x0104, 0x0105, 0x0106, 0x0107, 0x010C, 0x010D, 0x010E, 0x010F,
    0x0110, 0x0111, 0x0118, 0x0119, 0x011A, 0x011B, 0x0131, 0x0139,
    0x013A, 0x013D, 0x013E, 0x0141, 0x0142, 0x0143, 0x0144, 0x0147,
    0x0148, 0x0150, 0x0151, 0x0152, 0x0153, 0x0154, 0x0155, 0x0158,
    0x0159, 0x015A, 0x015B, 0x015E, 0x015F, 0x0160, 0x0161, 0x0162,
    0x0163, 0x0164, 0x0165, 0x016E, 0x016F, 0x0170, 0x0171, 0x0178,
    0x0179, 0x017A, 0x017B, 0x017C, 0x017D, 0x017E, 0x0192, 0x02C6,
    0x02C7, 0x02D8, 0x02D9, 0x02DA, 0x02DB, 0x02DC, 0x02DD, 0x03A9,
    0x03C0, 0x2013, 0x2014, 0x2018, 0x2019, 0x201A, 0x201C, 0x201D,
    0x201E, 0x2020, 0x2021, 0x2022, 0x2026, 0x2030, 0x2039, 0x203A,
    0x2044, 0x20AC, 0x2122, 0x2202, 0x2206, 0x220F, 0x2211, 0x221A,
    0x221E, 0x222B, 0x2248, 0x2260, 0x2264, 0x2265, 0x25CA, 0xF8FF,
    0xFB01, 0xFB02
  };


  /**
   * The default Processing character set.
   * <P>
   * This is the union of the Mac Roman and Windows ANSI (CP1250)
   * character sets. ISO 8859-1 Latin 1 is Unicode characters 0x80 -> 0xFF,
   * and would seem a good standard, but in practice, most P5 users would
   * rather have characters that they expect from their platform's fonts.
   * <P>
   * This is more of an interim solution until a much better
   * font solution can be determined. (i.e. create fonts on
   * the fly from some sort of vector format).
   * <P>
   * Not that I expect that to happen.
   */
  static public char[] CHARSET;
  static {
    CHARSET = new char[126-33+1 + EXTRA_CHARS.length];
    int index = 0;
    for (int i = 33; i <= 126; i++) {
      CHARSET[index++] = (char)i;
    }
    for (int i = 0; i < EXTRA_CHARS.length; i++) {
      CHARSET[index++] = EXTRA_CHARS[i];
    }
  };


  /**
   * ( begin auto-generated from PFont_list.xml )
   *
   * Gets a list of the fonts installed on the system. The data is returned
   * as a String array. This list provides the names of each font for input
   * into <b>createFont()</b>, which allows Processing to dynamically format
   * fonts. This function is meant as a tool for programming local
   * applications and is not recommended for use in applets.
   *
   * ( end auto-generated )
   *
   * @webref pfont
   * @usage application
   * @brief     Gets a list of the fonts installed on the system
   */
  static public String[] list() {
    loadFonts();
    String list[] = new String[fonts.length];
    for (int i = 0; i < list.length; i++) {
      list[i] = fonts[i].getName();
    }
    return list;
  }


  static public void loadFonts() {
    if (fonts == null) {
      GraphicsEnvironment ge =
        GraphicsEnvironment.getLocalGraphicsEnvironment();
      fonts = ge.getAllFonts();
    }
  }


  /**
   * Starting with Java 1.5, Apple broke the ability to specify most fonts.
   * This bug was filed years ago as #4769141 at bugreporter.apple.com. More:
   * <a href="http://dev.processing.org/bugs/show_bug.cgi?id=407">Bug 407</a>.
   */
  static public Font findFont(String name) {
    loadFonts();
   
    for (int i = 0; i < fonts.length; i++) {
        if (name.equals(fonts[i].getName())) {
            return fonts[i];
        }
    }

    return new Font(name, Font.PLAIN, 1);
  }


  //////////////////////////////////////////////////////////////


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
      int mbox3 = size * 3;
      lazyGraphics.setColor(Color.white);
      lazyGraphics.fillRect(0, 0, mbox3, mbox3);
      lazyGraphics.setColor(Color.black);
      lazyGraphics.drawString(String.valueOf(c), size, size * 2);

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
      topExtent = size*2 - minY;

      // offset from left of where coord was drawn
      leftExtent = minX - size;

      
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
  
  public Rect getStringBox (String string, GFont gfont)
  {
   
    TextLayout textLayout = new TextLayout (string, font, 
            lazyGraphics.getFontRenderContext());
    Rectangle2D bounds = textLayout.getBounds();

    int width  = (int) Math.ceil (bounds.getWidth());
    int height = (int) Math.ceil (bounds.getHeight());
    
    return new Rect (0, 0, width, height);
  }
  
  
  public GLFont createGLFont( GL2 gl2 ) {
      if ( glfont == null ){
          glfont = new GLFont(gl2, this);
      }
   
      return glfont;
  }
  
  private static GLFont glfont = null;
}

