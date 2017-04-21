/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.views;

import fr.ifp.kronosflow.geometry.Point2D;
import java.util.List;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFXSymbol;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

/**
 * to display symbols at location defined by a list of points.
 */
public class GPoints extends GObject {

    public GPoints() {
        setVisibility(GObject.DATA_VISIBLE | GObject.SYMBOLS_VISIBLE);
    }

    public void addPoint(Point2D pt, GColor color) {
        GSegment gPoint = new GSegment();

        gPoint.setUserData(pt);
        addSegment(gPoint);

        GStyle symbolStyle = new GStyle();
        symbolStyle.setForegroundColor(color);

        GImage square = new GFXSymbol(GFXSymbol.SYMBOL_SQUARE2);
        square.setStyle(symbolStyle);
        gPoint.setVertexImage(square);
    }

    public void addPoint(double[] pt, GColor color) {
        addPoint( new Point2D(pt[0],  pt[1]), color );
    }

    public void addPoint(double[] pt) {
        addPoint(pt, new GColor(100, 100, 150));

    }

    @Override
    protected void draw() {

        List<GSegment> segments = getSegments();
        if (segments == null) {
            return;
        }

        for (GSegment gPoint : getSegments()) {
            Point2D pt = (Point2D) gPoint.getUserData();
            gPoint.setWorldGeometry(pt.x(), pt.y());
        }
    }

}
