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
package stratifx.application.interaction;

import fr.ifp.jdeform.scene.Scene;
import java.util.Collection;
import java.util.List;

import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Triangle;
import fr.ifp.kronosflow.mesh.triangulation.Triangulation;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFXSymbol;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class TriangulateInteraction extends SectionInteraction {

    private GMarkers markers;

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
                symbolStyle.setForegroundColor(GColor.CYAN);
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

            GStyle style = new GStyle();
            style.setForegroundColor(GColor.CYAN);
            pointSet.setStyle(style);
        }

        void addOutline(Patch patch) {
            pointSet.setPatch(patch);
        }

    }

    public TriangulateInteraction(GScene scene) {
        super(scene);

    }

    @Override
    public boolean mouseEvent(GScene gscene, GMouseEvent event) {
        if (this.gscene != gscene) {
            return false;
        }

        switch (event.type) {
            case GMouseEvent.BUTTON_DOWN:
                Patch patch = getSelectedPatch(event.x, event.y);
                if (patch != null) {

                    Scene scene = createScene(patch);

                    // Create a graphic node for holding the interaction graphics
                    markers = new GMarkers();

                    gscene.add(markers);
                    markers.addOutline(scene.getSelected());

                    markers.redraw();

                    gscene.refresh();
                }
                break;

            case GMouseEvent.BUTTON_DRAG:
                break;

            case GMouseEvent.BUTTON_UP:

                if (null != markers) {

                    markers.removeSegments();;
                    markers.remove();

                    markers = null;
                    
                    gscene.refresh();
                }
                break;
   
        }

        return true;
    }

}
