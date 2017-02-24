/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.application.views;

import fr.ifp.kronosflow.geoscheduler.algo.DisplacementsBetween;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.warp.Displacement;
import fr.ifp.kronosflow.warp.IUIDDisplacements;
import java.util.List;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

public class GDisplacement extends GObject {

    IUIDDisplacements dBetween;

    GSegment border;

    GSegment originalBorder;

    GSegment[] displacements;

    public GDisplacement(IPolyline iPolyline, IUIDDisplacements dBetween) {
        setUserData(iPolyline);
        this.dBetween = dBetween;

        setVisibility(GObject.DATA_VISIBLE | GObject.ANNOTATION_INVISIBLE | GObject.SYMBOLS_INVISIBLE);

        border = new GSegment();
        addSegment(border);

        originalBorder = new GSegment();
        addSegment(originalBorder);

        displacements = new GSegment[iPolyline.size()];
        for (int i = 0; i < displacements.length; i++) {
            GStyle style = new GStyle();
            style.setLineWidth(1);
            displacements[i] = new GSegment();
            displacements[i].setStyle(style);
            addSegment(displacements[i]);
        }

    }

    public IPolyline getLine() {
        return (IPolyline) getUserData();
    }

    @Override
    protected void draw() {
        drawBorder();

        if (null != displacements) {
            drawDisplacements();
        }

    }

    private void drawBorder() {
        IPolyline line = getLine();

        List<ICurviPoint> pts = line.getPoints();

        int npts = (line.isClosed()) ? pts.size() + 1 : pts.size();
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];

        double[] w_pt = new double[2];

        int i = 0;
        for (ICurviPoint tp : pts) {
            line.getPosition(tp, w_pt);
            xpts[i] = w_pt[0];
            ypts[i] = w_pt[1];
            i++;
        }
        if (line.isClosed()) {
            ICurviPoint tp = pts.get(0);
            line.getPosition(tp, w_pt);
            xpts[npts - 1] = w_pt[0];
            ypts[npts - 1] = w_pt[1];
        }

        border.setWorldGeometry(xpts, ypts);
    }

    private void drawDisplacements() {

        IPolyline line = getLine();

        List<ICurviPoint> pts = line.getPoints();

        int npts = (line.isClosed()) ? pts.size() + 1 : pts.size();
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];

        double[] start;
        for (int i = 0; i < displacements.length; i++) {
            GSegment segment = displacements[i];

            ICurviPoint cp = pts.get(i);

            Displacement displacement = dBetween.getDisplacement(cp.getUID());

            start = displacement.getStart();
            double[] end = displacement.getTarget();

            segment.setWorldGeometry(start[0], start[1], end[0], end[1]);

            xpts[i] = start[0];
            ypts[i] = start[1];
        }

        if (line.isClosed()) {
            ICurviPoint cp = pts.get(0);
            Displacement displacement = dBetween.getDisplacement(cp.getUID());
            start = displacement.getStart();
            xpts[npts - 1] = start[0];
            ypts[npts - 1] = start[1];
        }

        originalBorder.setWorldGeometry(xpts, ypts);

    }

}
