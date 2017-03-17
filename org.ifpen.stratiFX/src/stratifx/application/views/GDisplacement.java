/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.application.views;

import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.warp.Displacement;
import fr.ifp.kronosflow.warp.IUIDDisplacements;
import java.util.Collection;
import java.util.List;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;

public class GDisplacement extends GObject {

    IUIDDisplacements dBetween;

    List<Displacement> displacements;

    GSegment border;

    GSegment originalBorder;

    GSegment[] gDisplacements;

    boolean closed = true;

    public GDisplacement(
            IUIDDisplacements dBetween,
            List<Displacement> displacements) {
        this.dBetween = dBetween;
        this.displacements = displacements;

        setVisibility(GObject.DATA_VISIBLE | GObject.ANNOTATION_INVISIBLE | GObject.SYMBOLS_INVISIBLE);

        border = new GSegment();
        addSegment(border);

        originalBorder = new GSegment();
        addSegment(originalBorder);

        if (null != displacements) {
            gDisplacements = new GSegment[displacements.size()];
            for (int i = 0; i < gDisplacements.length; i++) {
                GStyle style = new GStyle();
                style.setLineWidth(1);
                gDisplacements[i] = new GSegment();
                gDisplacements[i].setStyle(style);
                addSegment(gDisplacements[i]);
            }
        }

    }

    @Override
    protected void draw() {
        drawBorders();

        if (null != displacements) {
            drawDisplacements();
        }

    }

    private void drawBorders() {
        Collection<Displacement> pts = dBetween.getDisplacements();

        int npts = (closed) ? pts.size() + 1 : pts.size();
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];

        double[] xptsOri = new double[npts];
        double[] yptsOri = new double[npts];

      
        int i = 0;
        for (Displacement disp : pts) {

            xpts[i] = disp.getStart()[0];
            ypts[i] = disp.getStart()[1];

            xptsOri[i] = disp.getTarget()[0];
            yptsOri[i] = disp.getTarget()[1];
            i++;
        }
        if (closed) {

            xpts[npts - 1] = xpts[0];
            ypts[npts - 1] = ypts[0];

            xptsOri[npts - 1] = xptsOri[0];
            yptsOri[npts - 1] = yptsOri[0];
        }

        border.setWorldGeometry(xpts, ypts);

        originalBorder.setWorldGeometry(xptsOri, yptsOri);
    }

    private void drawDisplacements() {

        int npts = gDisplacements.length;

        double[] xpts = new double[npts];
        double[] ypts = new double[npts];

        for (int i = 0; i < npts; i++) {

            GSegment segment = gDisplacements[i];

            Displacement displacement = displacements.get(i);

            double[] start = displacement.getStart();
            double[] end = displacement.getTarget();

            segment.setWorldGeometry(start[0], start[1], end[0], end[1]);

            xpts[i] = start[0];
            ypts[i] = start[1];
            i++;
        }

    }

}
