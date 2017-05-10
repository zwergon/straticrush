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

import stratifx.application.interaction.ElongationInteraction;
import stratifx.application.interaction.PatchDisplacementsInteraction;
import java.net.URL;
import java.util.ResourceBundle;

import fr.ifp.jdeform.deformation.DeformationFactory.Kind;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.geology.FaultFeature;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.style.StyleManager;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import stratifx.application.GParameters;
import stratifx.application.IUIController;
import stratifx.application.StratiFXService;
import stratifx.application.UIAction;
import stratifx.application.interaction.DilatationInteraction;
import stratifx.application.interaction.HorizonMSInteraction;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.MasterSlaveInteraction;
import stratifx.application.interaction.InteractionUIAction;
import stratifx.application.interaction.NodeMoveInteraction;
import stratifx.application.interaction.RemoveUnitInteraction;
import stratifx.application.interaction.ResetGeometryInteraction;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.interaction.TopBorderInteraction;
import stratifx.application.interaction.TriangulateInteraction;
import stratifx.application.interaction.ZoomInteraction;
import stratifx.application.properties.PropertiesUIAction;
import stratifx.canvas.graphics.GWorldExtent;
import stratifx.canvas.graphics.IZoomHandler;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class PlotController
        implements
        Initializable,
        IUIController,
        IZoomHandler {

    @FXML
    private Canvas canvasId;

    @FXML
    private Group plotGroupId;

    @FXML
    private AnchorPane paneId;

    NumberAxis axisY;

    NumberAxis axisX;

    GFXScene gfxScene;

    GInteraction interaction_;

    double[] xy = new double[2];

    public GFXScene getGFXScene() {
        return gfxScene;
    }

    @FXML
    private void onPlotScrolled(ScrollEvent event) {

        double sx = 0;
        if (event.getDeltaY() < 0) {
            sx = plotGroupId.getScaleX() * 0.95;
        } else {
            sx = plotGroupId.getScaleX() * 1.05;
        }

        plotGroupId.setScaleX(sx);
        plotGroupId.setScaleY(sx);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        canvasId.addEventFilter(MouseEvent.ANY, (e) -> canvasId.requestFocus());

        gfxScene = new GFXScene(canvasId);
        gfxScene.setZoomHandler(this);

        GWorldExtent extent = gfxScene.getWorldExtent();

        double left = extent.left();
        double right = extent.right();
        double width = extent.getWidth();
        axisX = new NumberAxis(left, right, width / 10.);
        axisX.setMouseTransparent(true);
        axisX.setSide(Side.BOTTOM);
        axisX.setPrefWidth(canvasId.getWidth());
        axisX.setLayoutX(canvasId.getLayoutX());
        axisX.setLayoutY(canvasId.getLayoutY() + canvasId.getHeight() / 2.);
        plotGroupId.getChildren().add(axisX);

        double bottom = extent.bottom();
        double top = extent.top();
        double height = extent.getHeight();
        axisY = new NumberAxis(bottom, top, height / 10.);
        axisY.setMouseTransparent(true);
        axisY.setSide(Side.LEFT);
        axisY.setPrefHeight(canvasId.getHeight());
        axisY.setLayoutX(canvasId.getLayoutX());
        axisY.setLayoutY(canvasId.getLayoutY());
        plotGroupId.getChildren().add(axisY);

        Rectangle clipRectangle = new Rectangle(
                canvasId.getLayoutX(),
                canvasId.getLayoutY(),
                canvasId.getWidth(),
                canvasId.getHeight());
        paneId.setClip(clipRectangle);

    }

    public void initWorldExtent(double x0, double y0, double width, double height) {
        double w0[] = {x0, y0};
        double w1[] = {x0 + width, y0};
        double w2[] = {x0, y0 + height};

        gfxScene.initWorldExtent(w0, w1, w2);

        setWorldExtent(w0, w1, w2);
    }

    public void setWorldExtent(double x0, double y0, double width, double height) {
        double w0[] = {x0, y0};
        double w1[] = {x0 + width, y0};
        double w2[] = {x0, y0 + height};

        setWorldExtent(w0, w1, w2);
    }

    public void setWorldExtent(double[] w0, double[] w1, double[] w2) {

        gfxScene.setWorldExtent(w0, w1, w2);

        updateAxis();
    }

    private void updateAxis() {
        GWorldExtent wExtent = gfxScene.getWorldExtent();

        axisX.setLowerBound(wExtent.left());
        axisX.setUpperBound(wExtent.right());
        axisX.setTickUnit(Math.abs(wExtent.getWidth() / 10.));

        axisY.setLowerBound(wExtent.bottom());
        axisY.setUpperBound(wExtent.top());
        axisY.setTickUnit(Math.abs(wExtent.getHeight() / 10.));
    }

    /**
     * Install the specified interaction on this window. As a window can
     * administrate only one interaction at the time, the current interaction
     * (if any) is first stopped.
     *
     * @param interaction Interaction to install and start.
     */
    public void startInteraction(GInteraction interaction) {
        if (interaction_ != null) {
            stopInteraction();
        }

        interaction_ = interaction;
        if (null != interaction) {
            interaction_.start(gfxScene);
        }
    }

    /**
     * Stop the current interaction. The current interaction will get an ABORT
     * event so it has the possibility to do cleanup. If no interaction is
     * installed, this method has no effect.
     */
    public void stopInteraction() {
        // Nothing to do if no current interaction
        if (interaction_ == null) {
            return;
        }

        interaction_.stop(gfxScene);
        interaction_ = null;
    }

    private boolean gfxHandleMouse(int type, MouseEvent mouseEvent) {
        if (null != interaction_) {
            PickResult result = mouseEvent.getPickResult();
            if (result.getIntersectedNode().equals(canvasId)) {
                Point3D pt = result.getIntersectedPoint();

                GMouseEvent gevent = new GMouseEvent(
                        type,
                        getGFXButton(mouseEvent),
                        (int) pt.getX(),
                        (int) pt.getY(),
                        getGFXModifier(mouseEvent)
                );
                return interaction_.mouseEvent(gfxScene, gevent);
            }
        }
        return false;
    }

    private int getGFXButton(MouseEvent mouseEvent) {
        return GMouseEvent.BUTTON_1;
    }

    private int getGFXModifier(MouseEvent mouseEvent) {
        return 0;
    }

    @FXML
    private void onMouseClicked(MouseEvent mouseEvent) {
    }

    @FXML
    private void onMouseMoved(MouseEvent mouseEvent) {
    }

    @FXML
    private void onMousePressed(MouseEvent mouseEvent) {

        xy[0] = mouseEvent.getX();
        xy[1] = mouseEvent.getY();

        if (gfxHandleMouse(GMouseEvent.BUTTON_DOWN, mouseEvent)) {
            mouseEvent.consume();
        }
    }

    @FXML
    private void onMouseReleased(MouseEvent mouseEvent) {
        if (gfxHandleMouse(GMouseEvent.BUTTON_UP, mouseEvent)) {
            mouseEvent.consume();
        }
    }

    @FXML
    private void onMouseDragged(MouseEvent mouseEvent) {

        if (mouseEvent.isMiddleButtonDown()) {

            double[] dxy = new double[]{
                plotGroupId.getTranslateX() + mouseEvent.getX() - xy[0],
                plotGroupId.getTranslateY() + mouseEvent.getY() - xy[1]
            };

            plotGroupId.setTranslateX(dxy[0]);
            plotGroupId.setTranslateY(dxy[1]);

            xy[0] = mouseEvent.getX();
            xy[1] = mouseEvent.getY();
            return;
        }
        if (gfxHandleMouse(GMouseEvent.BUTTON_DRAG, mouseEvent)) {
            mouseEvent.consume();
        }
    }

    @FXML
    private void onMouseEntered(MouseEvent mouseEvent) {
    }

    @FXML
    private void onMouseExited(MouseEvent mouseEvent) {
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if (gfxHandleKey(GKeyEvent.KEY_PRESSED, keyEvent)) {
            keyEvent.consume();
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        if (gfxHandleKey(GKeyEvent.KEY_RELEASED, keyEvent)) {
            keyEvent.consume();
        }
    }

    private boolean gfxHandleKey(int type, KeyEvent keyEvent) {
        if (null != interaction_) {
            GKeyEvent gevent = new GKeyEvent(
                    type,
                    getGFXModifier(keyEvent),
                    getGFXKeyCode(keyEvent),
                    getGFXKeyChar(keyEvent),
                    GKeyEvent.KEY_LOCATION_STANDARD
            );
            return interaction_.keyEvent(gfxScene, gevent);

        }
        return false;
    }

    private int getGFXKeyCode(KeyEvent keyEvent) {

        KeyCode code = keyEvent.getCode();

        String name = code.getName();
        if (name.length() == 1) {
            return name.charAt(0);
        }

        switch (code) {
            case ESCAPE:
                return GKeyEvent.VK_ESCAPE;
            case SPACE:
                return GKeyEvent.VK_SPACE;
        }

        return 0;
    }

    private char getGFXKeyChar(KeyEvent keyEvent) {
        // TODO Auto-generated method stub
        return 0;
    }

    private int getGFXModifier(KeyEvent keyEvent) {
        int modifier = 0;
        if (keyEvent.isControlDown()) {
            modifier |= GKeyEvent.CTRL_MASK;
        }
        if (keyEvent.isMetaDown()) {
            modifier |= GKeyEvent.META_MASK;
        }
        if (keyEvent.isShiftDown()) {
            modifier |= GKeyEvent.SHIFT_MASK;
        }

        return modifier;
    }

    @Override
    public boolean handleAction(UIAction action) {

        switch (action.getType()) {
            case UIAction.ZOOMONEONE:
                gfxScene.unzoom();
                restoreZoom();
                break;

            case UIAction.ZOOMRECT:
                zoomRect();
                break;

            case InteractionUIAction.INTERACTION:
                return handleInteractionAction(action);

            case UIAction.PROPERTIES:
                return handlePropertyInteraction((PropertiesUIAction) action);
        }
        return true;
    }

    private void zoomRect() {
        startInteraction(new ZoomInteraction(gfxScene));
    }

    private void restoreZoom() {
        plotGroupId.setScaleX(1);
        plotGroupId.setScaleY(1);
        plotGroupId.setTranslateX(0);
        plotGroupId.setTranslateY(0);
    }

    private boolean handleInteractionAction(UIAction action) {

        InteractionUIAction uiAction = (InteractionUIAction) action;

        String deformationType = uiAction.getDeformationType();

        Style style = GParameters.getStyle();
        SceneStyle sceneStyle = new SceneStyle(style);
        if (deformationType.equals("VerticalShear")
                || deformationType.equals("FlexuralSlip")
                || deformationType.equals("MovingLS")) {
            sceneStyle.setGridType(GridType.LINE);
            sceneStyle.setNatureType(NatureType.EXPLICIT);
        } else {
            sceneStyle.setGridType(GridType.TRGL);
            sceneStyle.setNatureType(NatureType.EXPLICIT);
        }

        if (deformationType.equals("VerticalShear")
                || deformationType.equals("FlexuralSlip")
                || deformationType.equals("MovingLS")
                || deformationType.equals("ChainMail")
                || deformationType.equals("MassSpring")
                || deformationType.equals("Reset")) {
            style.setAttribute(Kind.DEFORMATION.toString(), deformationType);
        } else if (deformationType.equals("Dynamic")
                || deformationType.equals("Static")
                || deformationType.equals("StaticLS")
                || deformationType.equals("FEM2D")) {
            style.setAttribute(Kind.DEFORMATION.toString(), "NodeLinksDeformation");
            style.setAttribute(Kind.SOLVER.toString(), deformationType);
        } else if (deformationType.equals("Thermal")
                || deformationType.equals("Decompaction")) {
            style.setAttribute(Kind.DEFORMATION.toString(), "DilatationDeformation");
            style.setAttribute("DilatationType", deformationType);
        }

        String manipulatorType = uiAction.getManipulatorType();

        SectionInteraction interaction = InteractionFactory.getInstance()
                .createInteraction(manipulatorType, gfxScene);

        if (interaction != null) {
            interaction.setStyle(style);
            startInteraction(interaction);
            return true;
        }

        return false;
    }

    private boolean handlePropertyInteraction(PropertiesUIAction action) {

        Style style = GParameters.getStyle();
        SceneStyle sceneStyle = new SceneStyle(style);
        Section section = StratiFXService.instance.getSection();

        sceneStyle.setGridType(GridType.TRGL);
        sceneStyle.setNatureType(NatureType.EXPLICIT);
        List<FaultFeature> faults = section.getFeatures().getGeologicFeaturesByClass(
                FaultFeature.class);
        for (FaultFeature faultFeature : faults) {
            sceneStyle.setUnusualBehavior(section, faultFeature, true);
        }

        SectionInteraction interaction = null;
        switch (action.getProperty()) {
            case ELONGATION:
                interaction = new ElongationInteraction(gfxScene);
                break;
            default:
                break;
        }

        if (interaction != null) {
            interaction.setStyle(style);
            startInteraction(interaction);
            return true;
        }

        return false;

    }

    @Override
    public void update() {
        updateAxis();
    }

}
