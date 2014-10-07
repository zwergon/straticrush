package straticrush.view;

import java.awt.Insets;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GViewport;

public class Annotation extends GObject
{
  private Insets    insets_;
  private GSegment background_;
  private GSegment  title_;

  
  public Annotation (Insets insets)
  {
    insets_ = insets;
    
    background_ = new GSegment();
    GStyle backgroundStyle = new GStyle();
    backgroundStyle.setBackgroundColor (new GColor (1.0f, 1.0f, 0.9f, 0.8f));
    background_.setStyle (backgroundStyle);
    addSegment (background_);

    title_ = new GSegment();
    GStyle titleStyle = new GStyle();
    titleStyle.setForegroundColor (new GColor (100, 120, 120));
    title_.setStyle (titleStyle);
    title_.setText (new GText("Kronos - Sprint 1"));
    addSegment (title_);
    
    GStyle axisStyle = new GStyle();
    axisStyle.setForegroundColor (new GColor (100, 100, 100));
    axisStyle.setBackgroundColor (null);
    //axisStyle.setFont (new Font ("Dialog", Font.BOLD, 10));

    Axis horizontalAxis = new Axis (true, insets_);
    horizontalAxis.setStyle (axisStyle);
    add (horizontalAxis);
    
    Axis verticalAxis = new Axis (false, insets_);
    verticalAxis.setStyle (axisStyle);
    add (verticalAxis);
  }
  

  public static int[] createRectangle (int x0, int y0, int width, int height)
  {
    return new int[] {x0,               y0,
                      x0 + (width - 1), y0,
                      x0 + (width - 1), y0 + (height - 1),
                      x0,               y0 + (height - 1),
                      x0,               y0};
  }

  public void draw()
  {
    GViewport viewport = getScene().getViewport();

    int x0     = insets_.left;
    int y0     = insets_.top;
    int width  = viewport.getX3() - insets_.right - insets_.left + 1;
    int height = viewport.getY3() - insets_.bottom - insets_.top + 1;

    // Draw background
    background_.setGeometry (createRectangle (x0, y0, width, height));

    // Draw title
    title_.setGeometry (x0 + width / 2, y0 / 2);
  }
}
