package stratifx.application.interaction.edit;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchPoint;
import fr.ifp.kronosflow.model.topology.ContactGraph;
import fr.ifp.kronosflow.kernel.polyline.ICurviPoint;
import fr.ifp.kronosflow.kernel.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.views.GPoints;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GTransformer;
import stratifx.canvas.interaction.GMouseEvent;

import java.util.ArrayList;
import java.util.List;

public class MovePointsInteraction extends SectionInteraction {

    List<PatchPoint> ptsToMove;

    PolyLineGeometry geometry;

    Point2D target;

    final static int G_POINTS = 1;

    public MovePointsInteraction(GScene scene) {
        super(scene);

        target = new Point2D();
    }

    @Override
    public boolean mouseEvent(GScene scene, GMouseEvent event) {
        if (scene != gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                handleMousePress(event);
                break;

            case GMouseEvent.BUTTON_DRAG:
                handleMouseDrag(event);
                break;

            case GMouseEvent.BUTTON_UP:
                handleMouseRelease(event);
                break;

            case GMouseEvent.ABORT:
                break;
        }

        return true;
    }

    private void handleMouseRelease(GMouseEvent event){

        if ( (ptsToMove != null) && !ptsToMove.isEmpty() ) {
            for (PatchPoint pt : ptsToMove) {
                double[] newPos = new double[]{
                        target.x(),
                        target.y()
                };
                pt.getPatch().getBorder().setPosition(pt.getCurviPoint(), newPos);

            }

            ptsToMove = null;

            gscene.redraw();
        }


        for (GObject gobject : gObjects.values()) {
            gscene.remove(gobject);
        }
        gscene.refresh();
    }

    private void handleMouseDrag(GMouseEvent event){
        if ( ( null != ptsToMove ) && !ptsToMove.isEmpty() ){
            GTransformer transformer = gscene.getTransformer();

            double[] wXY = transformer.deviceToWorld(event.x, event.y);

            target.setPosition(wXY);
            //geometry.projectEuclidian( new Point2D(wXY), target);

            GPoints points = getGObject(G_POINTS);
            for( Point2D pts : points.getPoint2Ds() ){
                pts.setPosition(wXY);
            }
            points.redraw();

            gscene.refresh();
        }
    }

    private void handleMousePress(GMouseEvent event) {

        Patch selectedPatch = getSelectedPatch(event.x, event.y);
        if (selectedPatch != null) {

            ptsToMove = new ArrayList<>();

            {
                ICurviPoint cp = getSelectedPoint(selectedPatch, event.x, event.y);
                if (cp != null) {
                    ptsToMove.add(new PatchPoint(selectedPatch, cp));
                }
                geometry = new PolyLineGeometry(selectedPatch.getBorder());
            }

            ContactGraph contactGraph = getSection().getPatchLibrary().getContactGraph();
            for( Patch p : contactGraph.getConnected(selectedPatch) ){
                ICurviPoint cp = getSelectedPoint(p, event.x, event.y);
                if ( cp != null ){
                    ptsToMove.add(new PatchPoint(p, cp));
                }
            }

            LOGGER.debug( ptsToMove.size() + " coincident points found !", getClass());


            GPoints gPoints = new GPoints();
            gObjects.put(G_POINTS, gPoints);

            int i = 0;
            for (PatchPoint pt : ptsToMove ) {
                gPoints.addPoint(pt.getPosition(), GColor.YELLOW);
            }
            gscene.add(gPoints);;
            gPoints.redraw();


            gscene.refresh();
        }
    }
}
