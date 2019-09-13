/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.application.interaction;

import fr.ifp.kronosflow.deform.decompaction.ElongationDataCube;
import fr.ifp.kronosflow.deform.scene.Scene;
import fr.ifp.kronosflow.kernel.cube.Image;
import fr.ifp.kronosflow.kernel.geometry.RectD;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.kernel.polyline.PolyLineGeometry;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GColorMap;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

/**
 *
 * @author lecomtje
 */
public class ElongationInteraction extends SectionInteraction {

    GBox markers;
    ElongationDataCube elongationCube;

    public ElongationInteraction(GScene gfxScene) {
        super(gfxScene);
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

                    Scene scene = createCompositeScene(patch);

                    // Create a graphic node for holding the interaction graphics
                    markers = new ElongationInteraction.GBox();

                    Patch selectedPatch = scene.getSelected();

                    gscene.add(markers);

                    PolyLineGeometry geometry = new PolyLineGeometry(selectedPatch.getBorder());
                    RectD bbox = geometry.computeBoundingBox();

                    int[] bottomLeft = this.gscene.getTransformer().worldToDevice(new double[]{bbox.left, bbox.bottom});
                    int[] topRight = this.gscene.getTransformer().worldToDevice(new double[]{bbox.right, bbox.top});

                    //elongationCube = new ElongationApproximation();
                    elongationCube = new ElongationDataCube();
                    elongationCube.compute(
                            scene.getSelected(),
                            Math.abs(bottomLeft[0] - topRight[0]),
                            Math.abs(bottomLeft[1] - topRight[1])
                    );
                    
                    /*MedianCubeFilter filter = new MedianCubeFilter(elongationCube);
                    filter.compute(5);*/
                    
                    
                   

                    markers.setElongationCube(elongationCube);
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

    class GBox extends GObject {

        ElongationDataCube cube;

        private GSegment boundingBox;

        GColorMap colormap;

        public GBox() {
            setVisibility(DATA_VISIBLE);

            // Add style to object itself so it is inherited by segments
            GStyle lineStyle = new GStyle();
            setStyle(lineStyle);
        }

        public void setElongationCube(ElongationDataCube cube) {

            this.cube = cube;

            boundingBox = new GSegment();
            boundingBox.useTexture(true);
            GStyle bboxStyle = new GStyle();
            bboxStyle.setForegroundColor(new GColor(100, 100, 150));
            bboxStyle.setBackgroundColor(null);
            addSegment(boundingBox);
            boundingBox.setStyle(bboxStyle);

            colormap = new GColorMap();
            colormap.createFromName("Rainbow");
            colormap.setMinMax(cube.getMin(), cube.getMax());

        }

        @Override
        protected void draw() {

            RectD bbox = cube.getDescriptor().getDomain();

            double[] xx = new double[5];
            double[] yy = new double[5];

            xx[0] = bbox.left;
            yy[0] = bbox.bottom;
            xx[1] = bbox.right;
            yy[1] = bbox.bottom;
            xx[2] = bbox.right;
            yy[2] = bbox.top;
            xx[3] = bbox.left;
            yy[3] = bbox.top;
            xx[4] = bbox.left;
            yy[4] = bbox.bottom;

            boundingBox.setWorldGeometry(xx, yy);
        }

        @Override
        public GColor getColor(int x, int y) {

            Image image = cube.getDescriptor();

            double[] data = cube.getCube();

            double[] xy = gscene.getTransformer().deviceToWorld(x, y);

            int idx = image.getIndice(xy);

            if (image.isIndexValid(idx)) {
                return colormap.getColor(data[idx]);
            }
            
            return GColor.BLACK;
        }
    }

}
