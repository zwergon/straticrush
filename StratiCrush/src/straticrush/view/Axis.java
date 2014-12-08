package straticrush.view;

import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.Iterator;

import fr.ifp.kronosflow.geometry.Vector2D;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GText;
import no.geosoft.cc.graphics.GViewport;
import no.geosoft.cc.graphics.GWorldExtent;
import no.geosoft.cc.utils.NiceNumber;
import no.geosoft.cc.utils.NiceNumbers;

/**
 * A GObject representing one axis with annotation.
 */   
public class Axis extends GObject
{
  private Insets   insets_;
  private boolean  isTop_;


  public Axis (boolean isTop, Insets insets)
  {
    isTop_  = isTop;
    insets_ = insets;
  }
  
  
  public void draw()
  {
    removeSegments();
    
    // Get device coordinates
    GViewport viewport = getScene().getViewport();

    int vx0 = insets_.left;
    int vy0 = insets_.top;
    int vx1 = viewport.getX3() - insets_.right;
    int vy1 = viewport.getY3() - insets_.bottom;

    // Get annotation range
    GObject plot = (GObject) getScene().getUserData();
    if (plot == null) return;
    
    GWorldExtent worldExtent = plot.getScene().getWorldExtent();

    double[] w0 = worldExtent.get (0);
    double[] w1 = worldExtent.get (1);
    double[] w2 = worldExtent.get (2);      

    // Prepare axis values
    double from = isTop_ ? w0[0] : w2[1];
    double to   = isTop_ ? w1[0] : w0[1];

    int x0 = vx0;
    int y0 = vy0;
    int x1 = isTop_ ? vx1 : x0;
    int y1 = isTop_ ? y0 : vy1;

    double length = Vector2D.length(x0, y0, x1, y1);
    int n = (int) (length / 50.0);

    NiceNumbers niceNumbers = new NiceNumbers (from, to, n, true);      

    DecimalFormat format = new DecimalFormat ("0.00");
    
    for (Iterator i = niceNumbers.iterator(); i.hasNext(); ) {
      NiceNumber niceNumber = (NiceNumber) i.next();

      int rank = niceNumber.getRank();
      if (rank < 2) {
        int tickLength = rank == 0 ? 5 : 3;

        GSegment tick = new GSegment();
        int tx0 = isTop_ ? x0 + (int) Math.round (niceNumber.getPosition() * (x1 - x0)) : x0 - tickLength;
        int ty0 = isTop_ ? y0 - tickLength : y0 + (int) Math.round (niceNumber.getPosition() * (y1 - y0));
        int tx1 = isTop_ ? tx0 : x0; //without grid (rank == 0 ? vx1 : x0);
        int ty1 = isTop_ ? /*(rank == 0 ? vy1 : y0)*/ y0 : ty0;
        tick.setGeometry (tx0, ty0, tx1, ty1);

        if (rank == 0) {
          double value = niceNumber.getValue();
          GText text = new GText (format.format (value),
                                  isTop_ ? GPosition.TOP  : GPosition.LEFT);
          tick.setText (text);
        }

        addSegment (tick);
      }
    }
  }
}
