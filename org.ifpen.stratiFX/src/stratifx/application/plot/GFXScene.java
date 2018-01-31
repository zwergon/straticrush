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
package stratifx.application.plot;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.*;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.file.FileMeshPatch;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.topology.Border;
import fr.ifp.kronosflow.model.topology.Contact;
import fr.ifp.kronosflow.utils.LOGGER;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import stratifx.application.views.*;
import stratifx.canvas.graphics.*;

import java.nio.IntBuffer;
import java.util.*;

public class GFXScene extends GScene implements ICanvas {

    Canvas canvas;

    public GFXScene(Canvas canvas, GWorldExtent extent) {

        this.canvas = canvas;

        Bounds localB = canvas.getLayoutBounds();
        initialize(
                this,
                new GRect(
                        (int) localB.getMinX(), (int) localB.getMinY(),
                        (int) localB.getWidth(), (int) localB.getHeight()),
                extent
        );
    }

    public GFXScene(Canvas canvas) {

        this.canvas = canvas;

        Bounds localB = canvas.getLayoutBounds();
        initialize(
                this,
                new GRect(
                        (int) localB.getMinX(), (int) localB.getMinY(),
                        (int) localB.getWidth(), (int) localB.getHeight())
        );

    }


    public void destroyAll() {
        removeAll();
        removeSegments();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }


    @Override
    public void setClipArea(GRegion damageRegion) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clear(GRect extent) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        //gc.clearRect(extent.x, extent.y, extent.width, extent.height );

    }

    @Override
    public void render(GSegment segment, GStyle style) {

        double[][] xy = segment.getXY();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image img = null;
        GImage gimage = segment.getTexture();
        if (gimage != null) {

            gc.save();

            GRect rect = segment.getOwner().getRegion().getExtent();
            img = ((GFXTexture) gimage).getImageFX();

            gc.beginPath();
            gc.moveTo(xy[0][0], xy[1][0]);
            for (int i = 1; i < segment.size(); i++) {
                gc.lineTo(xy[0][i], xy[1][i]);
            }
            gc.closePath();
            gc.clip();
            gc.drawImage(img, rect.x, rect.y);

            gc.restore();
        } else {
            GColor bg = style.getBackgroundColor();
            if (null != bg) {

                float[] rgba = new float[3];
                bg.getRGBColorComponents(rgba);
                Color color = new Color(rgba[0], rgba[1], rgba[2], 1.);
                gc.setFill(color);
                gc.beginPath();
                gc.moveTo(xy[0][0], xy[1][0]);
                for (int i = 1; i < segment.size(); i++) {
                    gc.lineTo(xy[0][i], xy[1][i]);
                }
                gc.closePath();
                gc.fill();

            }
        }

        GColor fg = style.getForegroundColor();
        if (style.isLineVisible()) {
            float[] rgba = new float[3];
            fg.getRGBColorComponents(rgba);
            Color color = new Color(rgba[0], rgba[1], rgba[2], 1.);
            gc.setLineCap(StrokeLineCap.BUTT);
            gc.setLineJoin(StrokeLineJoin.BEVEL);
            gc.setStroke(color);
            gc.setLineWidth(style.getLineWidth());
            gc.strokePolyline(xy[0], xy[1], segment.size());
        }

    }

    @Override
    public void render(GText text, GStyle style) {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(GImage image) {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(int[] x, int[] y, GImage image) {
        
        GRect rectangle = image.getRectangle();

        WritableImage fxImg = new WritableImage(rectangle.width, rectangle.height);
        PixelWriter pw = fxImg.getPixelWriter();

        GColor gcolor = image.getStyle().getForegroundColor();
        int color = 0;
        if (gcolor == null) {
            return;
        }
        
        //map binary image into one argb color buffer.
        color = (gcolor.getAlpha() << 24)
                | (gcolor.getRed() << 16)
                | (gcolor.getGreen() << 8)
                | gcolor.getBlue();

 
        int[] imgData = image.getImageData();
        int[] data = new int[rectangle.width * rectangle.height];
        for (int j = 0; j < rectangle.height; j++) {   
            int offset = j * rectangle.width;
            for (int i = 0; i < rectangle.width; i++) {
                int idx = offset+i;
                if ( imgData[idx] == 1 ){
                    data[idx] = color; //foreground color
                }
                else {
                    data[idx] = 0; //background is transparent
                }
            }
        }

        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();
        pw.setPixels(0, 0, rectangle.width, rectangle.height, pixelFormat, data, 0, rectangle.width);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int i = 0; i < x.length; i++) {
            gc.drawImage(fxImg, x[i]+rectangle.x, y[i]+rectangle.y);
        }

    }

    @Override
    public GRect getStringBox(String string, GFont font) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Notify all listeners about change in this Shape.
     *
     */
    public void notifyViews(IControllerEvent<?> event) {

        for (GObject object : getChildren()) {
            if (object instanceof GView) {
                GView view = (GView) object;
                view.modelChanged(event);
            }
        }
    }


    /**
     * Method that goes though {@link KinObject} tree and collect list of all
     * children of this root {@link KinObject}.
     *
     * @param kobject the root {@link KinObject}
     * @param objects flat list of children
     */
    private void collectChildren(KinObject kobject, Collection<Object> objects) {
        List<KinObject> children = kobject.getChildren();
        if (children.isEmpty()) {
            return;
        }
        objects.addAll(children);
        for (KinObject child : children) {
            collectChildren(child, objects);
        }
    }

}
