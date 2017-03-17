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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.file.FileMeshPatch;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.topology.Border;
import fr.ifp.kronosflow.model.topology.Contact;
import fr.ifp.kronosflow.model.triangulation.TrglPatch;
import java.nio.IntBuffer;
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
import stratifx.application.views.GPaleoView;
import stratifx.application.views.GPartitionLineView;
import stratifx.application.views.GPatchIntervalView;
import stratifx.application.views.GPatchView;
import stratifx.canvas.graphics.GFXTexture;
import stratifx.application.views.GView;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFont;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GRect;
import stratifx.canvas.graphics.GRegion;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.GText;
import stratifx.canvas.graphics.GWorldExtent;
import stratifx.canvas.graphics.ICanvas;

public class GFXScene extends GScene implements ICanvas {

    Canvas canvas;

    static private Map<String, String> mapViews;

    static {
        mapViews = new HashMap<String, String>();
        registerView(Patch.class, GPatchView.class);
        registerView(MeshPatch.class, GPatchView.class);
        registerView(ExplicitPatch.class, GPatchView.class);
        registerView(CompositePatch.class, GPatchView.class);
        registerView(FileMeshPatch.class, GPatchView.class);
        registerView(TrglPatch.class, GPatchView.class);
        registerView(PatchInterval.class, GPatchIntervalView.class);
        registerView(FeatureGeolInterval.class, GPatchIntervalView.class);
        registerView(Contact.class, GPartitionLineView.class);
        registerView(Border.class, GPartitionLineView.class);
        registerView(Paleobathymetry.class, GPaleoView.class);
    }

    static public void registerView(Class<?> object_class, Class<?> view_class) {
        mapViews.put(object_class.getCanonicalName(), view_class.getCanonicalName());
    }

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

    public GView createView(Object object) {

        if (object == null) {
            return null;
        }
        GView view = null;
        try {
            /*
			 * TODO go through class inheritance to find the first ascending 
			 * class valid to create a GView
             */
            String key = object.getClass().getCanonicalName();

            if (!mapViews.containsKey(key)) {
                return null;
            }

            Class<?> c1 = Class.forName(mapViews.get(key));
            if (c1 == null) {
                return null;
            }
            view = (GView) c1.newInstance();
            if (null != view) {
                add(view);
                view.setModel(object);
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        return view;
    }

    public Collection<GView> getViews() {
        Collection<GView> views = new ArrayList<GView>();
        for (GObject object : getChildren()) {
            if (object instanceof GView) {
                views.add((GView) object);
            }
        }

        return views;
    }

    public void destroyViews(Object object) {

        Collection<Object> objects = new ArrayList<Object>();
        objects.add(object);
        if (object instanceof KinObject) {
            KinObject kobject = (KinObject) object;
            collectChildren(kobject, objects);
        }

        for (GView view : getViews()) {
            for (Object o : objects) {
                if (o == view.getUserData()) {
                    remove(view);
                }
            }
        }
    }

    public void destroyView(GView view) {
        remove(view);
    }

    public void destroyAllViews() {
        for (GView view : getViews()) {
            destroyView(view);
        }
    }

    public void destroyAll() {
        removeAll();
        removeSegments();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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

    /**
     * Notify all listeners about change in this Shape.
     *
     * @param Event Describe the change in the Shape.
     */
    public void notifyViews(IControllerEvent<?> event) {
        for (GObject object : getChildren()) {
            if (object instanceof GView) {
                GView view = (GView) object;
                view.modelChanged(event);
            }
        }
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

}
