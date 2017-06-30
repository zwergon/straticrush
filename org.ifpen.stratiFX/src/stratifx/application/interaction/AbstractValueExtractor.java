/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.geometry.Vector2D;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.polyline.IGeometryProvider;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author lecomtje
 */
public abstract class AbstractValueExtractor {

    Mesh2D mesh;

    IGeometryProvider geomProvider;

    double isoValue;

    Collection<PolyLine> polyline = new ArrayList<>();

    public AbstractValueExtractor(Mesh2D mesh, double isoValue) {
        this.mesh = mesh;
        this.isoValue = isoValue;
        this.geomProvider = mesh.getGeometryProvider();

    }

    public Collection<PolyLine> getPolyLine() {
        return polyline;
    }

    /**
     * Construct and fill a list of {@link PolyLine} with isovalues lines.
     */
    public void buildLines() {

        IGeometryProvider geomProvider = mesh.getGeometryProvider();

        LinkedList<IHandle> alreadySeen = new LinkedList<>(mesh.getCells());

        EdgePoint[] ep = new EdgePoint[2];
        while (!alreadySeen.isEmpty()) {

            Cell cell = (Cell) alreadySeen.pop();
            if (findCellSegment(cell, ep)) {

                List<Point2D> pts = followTheLine(ep, alreadySeen);

                ExplicitPolyLine line = new ExplicitPolyLine();

                LOGGER.info("Add line with " + pts.size() + " points", getClass());
                line.initialize(pts);
                polyline.add(line);

            }
          
        }
    }

    /**
     * This class hold an extremity of the segment resulting from isoValue
     * intersection with a cell
     *
     * @see #findCellSegment
     */
    class EdgePoint {

        Cell cell;
        double[] pt;

        public EdgePoint(Cell cell, double[] pt) {
            this.cell = cell;
            this.pt = pt;
        }

        private boolean isSameAs(EdgePoint ep) {
            return Vector2D.length(pt, ep.pt) < 1e-3;
        }

    }

    /**
     * Find if this {@link Cell} intersects the isoValue and return the
     * corresponding segment as 2 EdgePoint.
     *
     * THIS WORKS WITH CELLS WITH ONLY ONE INTERSECTION SEGMENT!
     *
     * @see EdgePoint
     */
    protected abstract boolean findCellSegment(Cell cell, EdgePoint[] segLine);

    /**
     * Follows the line without discontinuities from one segment using mesh
     * neighborhood.
     */
    private List<Point2D> followTheLine(EdgePoint[] ep, Collection<IHandle> availableCells) {

        List<Point2D> pts = new ArrayList<>();
        pts.add(new Point2D(ep[0].pt));
        pts.add(new Point2D(ep[1].pt));

        /* 
         follow against the first extremity (in one direction) of the initial segment ep
         and prepend isoValue segments to the current line
         */
        int foundNewEdge;
        do {

            foundNewEdge = 0;

            for (UID uid : mesh.getCellNeighbors(ep[0].cell.getUID())) {

                Cell cell = (Cell) mesh.getCell(uid);

                if (!availableCells.contains(cell)) {
                    continue;
                }

                EdgePoint[] segLine = new EdgePoint[2];
                if (findCellSegment(cell, segLine)) {

                    if (segLine[0].isSameAs(segLine[1])) {
                        availableCells.remove(cell);
                        continue;
                    }

                    if (segLine[0].isSameAs(ep[0])) {
                        pts.add(0, new Point2D(segLine[1].pt));
                        ep[0] = segLine[1];
                        foundNewEdge++;
                        availableCells.remove(cell);
                    } else if (segLine[1].isSameAs(ep[0])) {
                        pts.add(0, new Point2D(segLine[0].pt));
                        ep[0] = segLine[0];
                        foundNewEdge++;
                        availableCells.remove(cell);
                    }
                }

            }
        } while (foundNewEdge != 0);

        /* 
         follow against the second extremity (in the other direction) of the initial segment ep
         and append isoValue segments to the current line
         */
        do {

            foundNewEdge = 0;
            for (UID uid : mesh.getCellNeighbors(ep[1].cell.getUID())) {

                Cell cell = (Cell) mesh.getCell(uid);

                if (!availableCells.contains(cell)) {
                    continue;
                }

                EdgePoint[] segLine = new EdgePoint[2];
                if (findCellSegment(cell, segLine)) {

                    if (segLine[0].isSameAs(segLine[1])) {
                        availableCells.remove(cell);
                        continue;
                    }

                    if (segLine[0].isSameAs(ep[1])) {
                        pts.add(new Point2D(segLine[1].pt));
                        ep[1] = segLine[1];
                        foundNewEdge++;
                        availableCells.remove(cell);
                    } else if (segLine[1].isSameAs(ep[1])) {
                        pts.add(new Point2D(segLine[0].pt));
                        ep[1] = segLine[0];
                        foundNewEdge++;
                        availableCells.remove(cell);
                    }
                }
            }
        } while (foundNewEdge != 0);

        return pts;
    }
}
