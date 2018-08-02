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

import java.awt.Color;
import java.util.Random;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.controllers.property.PropertyEvent;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.kernel.property.Property;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.tooltip.ITooltipInfo;

public class GPatchView extends GView implements ITooltipInfo {

    GPolyline border = null;

    boolean withPatchGrid = true;

    public GPatchView() {
    }

    @Override
    public void setModel(Object object) {
        setUserData(object);

        Patch patch = (Patch) object;
        setName(patch.getName());

        createBorder(patch);

        if (withPatchGrid & (patch instanceof IMeshProvider)) {
            GMesh gMesh = new GMesh(((IMeshProvider) patch).getMesh());
            add(gMesh);

            gMesh.draw();
        }

    }

    public GObject getBorder() {
        return border;
    }

    public GColor getPatchColor() {

        Patch patch = getObject();

        Color aColor = patch.getColor();

        GColor patchColor;
        if (aColor != null) {
            patchColor = new GColor(aColor.getRed(), aColor.getGreen(), aColor.getBlue(), aColor.getAlpha());
        } else {
            patchColor = getRandomPastelColor();
        }

        return patchColor;
    }

    private static GColor getRandomPastelColor() {
        Random r = new Random();
        return GColor.getHSBColor(r.nextFloat(), (float) (0.1 + 0.2 * r.nextFloat()),
                (float) (0.3 + 0.5 * r.nextFloat()));
    }

    protected void createBorder(Patch patch) {

        if (null != patch.getBorder()) {
            border = new GPolyline(patch.getBorder());
            border.setTooltipInfo(this);


            GStyle style = new GStyle();
            style.setBackgroundColor(getPatchColor());
            style.setForegroundColor(GColor.black);
            border.setStyle(style);

            add(border);
        }

    }

    public Patch getObject() {
        return (Patch) getUserData();
    }

    @Override
    public void styleChanged(Style style) {

        if ( border == null ){
            return;
        }

        GStyle gstyle = border.getStyle();

        DisplayStyle displayStyle = new DisplayStyle(style);
        if ( displayStyle.getWithLines() ){
            if ( !gstyle.isLineVisible() ){
                gstyle.setLineStyle(GStyle.LINESTYLE_SOLID);
            }
        } else {
            if ( gstyle.isLineVisible() ) {
                gstyle.setLineStyle(GStyle.LINESTYLE_INVISIBLE);
            }
        }

        if ( displayStyle.getWithSolid() ){
            if ( gstyle.getBackgroundColor() == null ){
                gstyle.setBackgroundColor(getPatchColor());
            }
        } else {
            if ( gstyle.getBackgroundColor() != null ) {
                gstyle.unsetBackgroundColor();
            }
        }

        if ( null != border ){
            border.setVisibility( displayStyle.getWithSymbol() ? SYMBOLS_VISIBLE : SYMBOLS_INVISIBLE );
            border.setVisibility( displayStyle.getWithAnnotation() ? ANNOTATION_VISIBLE : ANNOTATION_INVISIBLE );
        }

        redraw();

    }

    @Override
    public void modelChanged(IControllerEvent<?> event) {

        if (event instanceof PropertyEvent) {
            PropertyEvent propEvent = (PropertyEvent) event;
            
            Object eventObject = propEvent.getObject();
            
            GProperty gProperty = null;
            
            if ( null != eventObject ){
                Property property = (Property)eventObject;
                gProperty = new GProperty(property, getObject());
                
            }
            border.setProperty(gProperty);
            

        }
        
        redraw();

        
    }

    @Override
    public String getInfo(int x, int y) {
      Patch patch = getObject();
      return patch.getName();  
    }

}
