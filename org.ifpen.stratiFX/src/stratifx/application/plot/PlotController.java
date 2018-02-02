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

import fr.ifp.jdeform.deformation.DeformationFactory.Kind;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.property.EnumProperty;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.utils.LOGGER;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import stratifx.application.caller.EventUIAction;
import stratifx.application.interaction.*;
import stratifx.application.main.GParameters;
import stratifx.application.main.IUIController;
import stratifx.application.main.StratiFXService;
import stratifx.application.main.UIAction;
import stratifx.application.properties.PropertiesUIAction;
import stratifx.application.views.GView;
import stratifx.application.views.StyleUIAction;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GWorldExtent;
import stratifx.canvas.graphics.ICallbackHandler;
import stratifx.canvas.graphics.tooltip.GTooltipTimer;
import stratifx.canvas.graphics.tooltip.ITooltipAction;
import stratifx.canvas.graphics.tooltip.ITooltipInfo;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GKeyEvent;
import stratifx.canvas.interaction.GMouseEvent;

public class PlotController
        extends Pane
        implements
        IUIController,
        ICallbackHandler,
        ITooltipAction {

    private Canvas canvasId;

    NumberAxis axisY;

    NumberAxis axisX;

    GFXScene gfxScene;

    GInteraction interaction_;

    Tooltip tooltip;

    GTooltipTimer timer;

    double[] xy = new double[2];

    public GFXScene getGFXScene() {
        return gfxScene;
    }

    public void initialize(double w, double h) {

        canvasId = new Canvas();
        getChildren().add(canvasId);
        canvasId.setWidth(w);
        canvasId.setHeight(h);

        gfxScene = new GFXScene(canvasId);
        gfxScene.setCallbackHandler(this);

        GWorldExtent extent = gfxScene.getWorldExtent();

        double left = extent.left();
        double right = extent.right();
        double width = extent.getWidth();
        axisX = new NumberAxis(left, right, width / 10.);
        axisX.setMouseTransparent(true);
        axisX.setSide(Side.BOTTOM);
        axisX.setPrefWidth(canvasId.getWidth() - 20);
        axisX.setLayoutX(canvasId.getLayoutX() + 10);
        axisX.setLayoutY(canvasId.getLayoutY() + canvasId.getHeight() / 2.);
        getChildren().add(axisX);

        double bottom = extent.bottom();
        double top = extent.top();
        double height = extent.getHeight();
        axisY = new NumberAxis(bottom, top, height / 10.);
        axisY.setMouseTransparent(true);
        axisY.setSide(Side.LEFT);
        axisY.setPrefHeight(canvasId.getHeight() - 20);
        axisY.setLayoutX(canvasId.getLayoutX());
        axisY.setLayoutY(canvasId.getLayoutY() + 10);
        getChildren().add(axisY);


        setOnMouseEntered(this::onMouseEntered);
        setOnMouseExited(this::onMouseExited);
        setOnMouseDragged(this::onMouseDragged);
        setOnMouseClicked(this::onMouseClicked);
        setOnMouseMoved(this::onMouseMoved);
        setOnMousePressed(this::onMousePressed);
        setOnMouseReleased(this::onMouseReleased);
        setOnScroll(this::onPlotScrolled);
        setOnKeyPressed(this::onKeyPressed);
        setOnKeyReleased(this::onKeyReleased);
        widthProperty().addListener(this::changeWidth);
        heightProperty().addListener(this::changeHeight);

        canvasId.addEventFilter(MouseEvent.ANY, (e) -> canvasId.requestFocus());


    }

    private void changeWidth(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        canvasId.setWidth(newValue.doubleValue());

        Bounds localB = canvasId.getLayoutBounds();
        gfxScene.setViewport(
                (int) localB.getMinX(), (int) localB.getMinY(),
                (int) localB.getWidth(), (int) localB.getHeight());

        gfxScene.refresh();

        axisX.setPrefWidth(canvasId.getWidth() - 20);
        axisX.setLayoutX(canvasId.getLayoutX() + 10);
        axisY.setLayoutX(canvasId.getLayoutX());
    }

    private void changeHeight(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        canvasId.setHeight(newValue.doubleValue());

        Bounds localB = canvasId.getLayoutBounds();
        gfxScene.setViewport(
                (int) localB.getMinX(), (int) localB.getMinY(),
                (int) localB.getWidth(), (int) localB.getHeight());

        gfxScene.refresh();

        axisY.setPrefHeight(canvasId.getHeight() - 20);
        axisX.setLayoutY(canvasId.getLayoutY() + canvasId.getHeight() / 2.);
        axisY.setLayoutY(canvasId.getLayoutY() + 10);
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
        switch (mouseEvent.getButton()) {
            case SECONDARY:
                return GMouseEvent.BUTTON_3;
            case MIDDLE:
                return GMouseEvent.BUTTON_2;
            default:
                break;
        }
        return GMouseEvent.BUTTON_1;
    }

    private void onPlotScrolled(ScrollEvent event) {

        if (event.getDeltaY() < 0) {
            gfxScene.zoom(0.95);
        } else {
            gfxScene.zoom(1.05);
        }

    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        resetTooltip();
        setTooltipPos(mouseEvent);

        if (mouseEvent.isMiddleButtonDown()) {
            mouseEvent.consume();
            return;
        }
    }

    private void onMouseMoved(MouseEvent mouseEvent) {
        resetTooltip();
        setTooltipPos(mouseEvent);

        if (mouseEvent.isMiddleButtonDown()) {
            mouseEvent.consume();
            return;
        }
    }

    private void onMousePressed(MouseEvent mouseEvent) {

        resetTooltip();
        setTooltipPos(mouseEvent);


        if (mouseEvent.isMiddleButtonDown()) {

            xy[0] = mouseEvent.getX();
            xy[1] = mouseEvent.getY();

            mouseEvent.consume();
            return;
        }


        if (gfxHandleMouse(GMouseEvent.BUTTON_DOWN, mouseEvent)) {
            mouseEvent.consume();
        }
    }

    private void onMouseReleased(MouseEvent mouseEvent) {

        if (mouseEvent.isMiddleButtonDown()) {
            mouseEvent.consume();
            return;
        }

        resetTooltip();
        if (gfxHandleMouse(GMouseEvent.BUTTON_UP, mouseEvent)) {
            mouseEvent.consume();
        }
    }

    private void onMouseDragged(MouseEvent mouseEvent) {

        resetTooltip();
        setTooltipPos(mouseEvent);

        if (mouseEvent.isMiddleButtonDown()) {

            double[] dxy = new double[]{
                    mouseEvent.getX() - xy[0],
                    mouseEvent.getY() - xy[1]
            };

            gfxScene.pan((int) dxy[0], (int) dxy[1]);

            xy[0] = mouseEvent.getX();
            xy[1] = mouseEvent.getY();

            mouseEvent.consume();
            return;
        }

        if (gfxHandleMouse(GMouseEvent.BUTTON_DRAG, mouseEvent)) {
            mouseEvent.consume();
        }

    }

    private void onMouseEntered(MouseEvent mouseEvent) {
        if (null == timer) {
            timer = new GTooltipTimer();
            timer.setAction(this);
            setTooltipPos(mouseEvent);
            timer.start();
        }
    }

    private void onMouseExited(MouseEvent mouseEvent) {

        try {
            if ((timer != null) && (timer.isAlive())) {
                timer.canStop();
                timer.wait();
            }
        } catch (Exception ex) {
        } finally {
            timer = null;
        }
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        resetTooltip();
        if (gfxHandleKey(GKeyEvent.KEY_PRESSED, keyEvent)) {
            keyEvent.consume();
        }
    }

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

    private int getGFXModifier(MouseEvent mouseEvent) {
        return 0;
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
                break;

            case UIAction.ZOOMRECT:
                startInteraction(new ZoomInteraction(gfxScene));
                break;

            case UIAction.INTERACTION:
                return handleInteractionAction(action);

            case UIAction.PROPERTIES:
                return handlePropertyInteraction((PropertiesUIAction) action);

            case UIAction.STYLE:
                return handleStyleAction((StyleUIAction) action);

            case UIAction.EVENT:
                return handleEventAction((EventUIAction) action);

            case UIAction.OPEN:
                return handleOpenAction();
        }
        return true;
    }

    private boolean handleOpenAction() {

        gfxScene.destroyAll();

        Section section = StratiFXService.instance.getSection();

        GView view = GViewsFactory.createView(section);
        if (null != view) {
            gfxScene.add(view);

            PatchLibrary patchLib = section.getPatchLibrary();
            RectD bbox = patchLib.getBoundingBox();
            bbox.inset(-bbox.width() / 10., -bbox.height() / 10.);
            initWorldExtent(bbox.left, bbox.top, bbox.width(), bbox.height());

            gfxScene.refresh();
        } else {
            LOGGER.error("unable to create view for Section " + section.getName(), getClass());
        }

        return false;
    }

    private boolean handleStyleAction(StyleUIAction action) {
        gfxScene.notifyViews(action.getData());
        gfxScene.refresh();
        return false;
    }

    private boolean handleEventAction(EventUIAction action) {
        gfxScene.notifyViews(action.getData());
        gfxScene.refresh();
        return false;
    }


    private boolean handleInteractionAction(UIAction action) {

        InteractionUIAction uiAction = (InteractionUIAction) action;

        String deformationType = uiAction.getDeformationType();

        Style style = GParameters.getStyle();
        SceneStyle sceneStyle = new SceneStyle(style);
        /*if (deformationType.equals("VerticalShear")
        || deformationType.equals("FlexuralSlip")
        || deformationType.equals("MovingLS")
        || deformationType.equals("Triangulation")
        || deformationType.equals("StratiGrid")) {
        sceneStyle.setGridType("None");
        } else {
        sceneStyle.setGridType("Trgl");
        }*/
        if (deformationType.equals("Dynamic")
                || deformationType.equals("Static")
                || deformationType.equals("StaticLS")
                || deformationType.equals("FEM2D")) {
            style.setAttribute(Kind.DEFORMATION.toString(), "NodeLinksDeformation");
            style.setAttribute(Kind.SOLVER.toString(), deformationType);
        } else if (deformationType.equals("Thermal")
                || deformationType.equals("Decompaction")) {
            style.setAttribute(Kind.DEFORMATION.toString(), "DilatationDeformation");
            style.setAttribute("DilatationType", deformationType);
        } else {
            style.setAttribute(Kind.DEFORMATION.toString(), deformationType);
        }

        String manipulatorType = uiAction.getManipulatorType();

        SectionInteraction interaction = InteractionFactory.getInstance()
                .createInteraction(manipulatorType, gfxScene);

        if (interaction != null) {
            interaction.setStyle(style);
            startInteraction(interaction);
            return true;
        } else {
            LOGGER.error("unable to create interaction " + manipulatorType, getClass());
        }

        return false;
    }

    private boolean handlePropertyInteraction(PropertiesUIAction action) {

        if (action.getData() != EnumProperty.ELONGATION) {
            return false;
        }

        Style style = GParameters.getStyle();
        SceneStyle sceneStyle = new SceneStyle(style);
        Section section = StratiFXService.instance.getSection();
        
        /*sceneStyle.setGridType(GridType.TRGL);
        sceneStyle.setNatureType(NatureType.EXPLICIT);
        List<FaultFeature> faults = section.getFeatures().getGeologicFeaturesByClass(
        FaultFeature.class);
        for (FaultFeature faultFeature : faults) {
        sceneStyle.setUnusualBehavior(section, faultFeature, true);
        }*/

        SectionInteraction interaction = null;
        switch (action.getData()) {
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
    public void call() {
        updateAxis();
    }


    @Override
    public void show(int x, int y) {

        GObject gObject = gfxScene.find(x, y);
        if (gObject != null) {
            ITooltipInfo info = gObject.getTooltipInfo();
            if (info != null) {

                Point2D scenePos = canvasId.localToScreen(x, y);
                tooltip = new Tooltip();
                tooltip.setText(info.getInfo(x, y));
                tooltip.show(
                        StratiFXService.instance.getPrimaryStage(),
                        scenePos.getX(), scenePos.getY());

            }
        }

    }

    @Override
    public void hide() {
        if (null != tooltip) {
            tooltip.hide();
        }
    }


    private void resetTooltip() {
        if ((null != tooltip) && tooltip.isShowing()) {
            tooltip.hide();
        }

        if (timer != null) {
            timer.reset();
        }
    }

    private void setTooltipPos(MouseEvent mouseEvent) {
        if (timer != null) {
            Point2D pt = canvasId.screenToLocal(mouseEvent.getScreenX(), mouseEvent.getScreenY());
            timer.setPos((int) pt.getX(), (int) pt.getY());
        }
    }

}
