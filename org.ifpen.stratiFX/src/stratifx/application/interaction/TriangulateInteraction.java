package stratifx.application.interaction;

import java.util.Collection;
import java.util.List;

import fr.ifp.jdeform.controllers.scene.Scene;
import fr.ifp.jdeform.controllers.scene.SceneBuilder;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Triangle;
import fr.ifp.kronosflow.mesh.triangulation.Triangulation;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.application.GParameters;
import stratifx.application.views.GPatchView;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFXSymbol;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class TriangulateInteraction implements GInteraction {

    private GScene gscene;
    private GMarkers markers;
    private Patch composite;

    boolean first = true;

    Triangulation triangulation = null;

    private class GPointSet extends GObject {

        private GSegment[] lines_;

        public GPointSet() {
            setVisibility(DATA_VISIBLE | SYMBOLS_VISIBLE);

            // Add style to object itself so it is inherited by segments
            GStyle lineStyle = new GStyle();
            setStyle(lineStyle);
        }

        public void setPatch(Patch patch) {

            List<Point2D> inner = null;
            if (patch instanceof CompositePatch) {
                CompositePatch composite = (CompositePatch) patch;
                inner = composite.getInnerPoints();
            }

            removeSegments();
            triangulation = new Triangulation();
            triangulation.setBeautify(false);
            if (inner != null) {
                triangulation.addInnerPoints(inner);
            }

            triangulation.execute(patch.getBorder().getPoints2D());

            Collection<IHandle> triangles = triangulation.getCells();
            System.out.println("n triangles " + triangles.size());

            lines_ = new GSegment[triangles.size()];
            for (int i = 0; i < triangles.size(); i++) {
                lines_[i] = new GSegment();
                addSegment(lines_[i]);

                GStyle symbolStyle = new GStyle();
                symbolStyle.setForegroundColor(new GColor(0, 0, 255));
                symbolStyle.setBackgroundColor(new GColor(0, 0, 255));
                GImage square = new GFXSymbol(GFXSymbol.SYMBOL_SQUARE1);
                square.setStyle(symbolStyle);

                lines_[i].setVertexImage(square);
            }
        }

        @Override
        protected void draw() {
            if (null == triangulation) {
                return;
            }
            double[] xy = new double[8];
            int i = 0;
            for (IHandle handle : triangulation.getCells()) {
                Triangle trgl = (Triangle) handle;

                Node n1 = (Node) triangulation.getNode(trgl.getNode(0));
                xy[0] = n1.x();
                xy[1] = n1.y();

                Node n2 = (Node) triangulation.getNode(trgl.getNode(1));
                xy[2] = n2.x();
                xy[3] = n2.y();

                Node n3 = (Node) triangulation.getNode(trgl.getNode(2));
                xy[4] = n3.x();
                xy[5] = n3.y();

                xy[6] = n1.x();
                xy[7] = n1.y();
                lines_[i++].setWorldGeometryXY(xy);
            }
        }
    }

    private class GMarkers extends GObject {

        GPointSet pointSet;

        public GMarkers() {
            super("Interaction");
            setVisibility(DATA_VISIBLE | SYMBOLS_VISIBLE);

            pointSet = new GPointSet();
            add(pointSet);
        }

        void addOutline(Patch patch) {

            GPolyline borderLine = new GPolyline(patch.getBorder());

            GStyle style = new GStyle();
            style.setBackgroundColor(GColor.CYAN);
            borderLine.setStyle(style);

            add(borderLine);

            pointSet.setPatch(patch);
        }

    }

    public TriangulateInteraction(GScene scene, String type) {
        gscene = scene;

        // Create a graphic node for holding the interaction graphics
        markers = new GMarkers();

    }

    @Override
    public boolean start(GScene scene) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean stop(GScene scene) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseEvent(GScene gscene, GMouseEvent event) {
        if (this.gscene != gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                GSegment selected = gscene.findSegment(event.x, event.y);
                if (selected != null) {
                    GObject gobject = selected.getOwner();
                    if (gobject instanceof GPatchView) {

                        Patch patch = ((GPatchView) gobject).getObject();

                        Scene scene = SceneBuilder.createDefaultScene(patch, GParameters.getStyle());
                        composite = scene.getSelected();

                        gscene.add(markers);
                        markers.addOutline(composite);

                        markers.redraw();

                        gscene.refresh();
                    }
                }
                break;

            case GMouseEvent.BUTTON_DRAG:
                break;

            case GMouseEvent.BUTTON_UP:

                if (null != composite) {

                    composite.remove();
                    markers.removeSegments();;
                    markers.remove();

                    composite = null;

                }
                break;

            case GMouseEvent.ABORT:
                break;
        }

        gscene.refresh();

        return true;
    }

    @Override
    public boolean keyEvent(GScene scene, GKeyEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

}
