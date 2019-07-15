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
package stratifx.application.main;

import fr.ifp.kronosflow.controllers.ControllerEventList;
import fr.ifp.kronosflow.controllers.IControllerService;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.controllers.events.UpdateEvent;
import fr.ifp.kronosflow.controllers.property.PropertyController;
import fr.ifp.kronosflow.controllers.property.PropertyControllerCaller;
import fr.ifp.kronosflow.deform.decompaction.PorosityComputer;
import fr.ifp.kronosflow.deform.deformation.DeformationFactory;
import fr.ifp.kronosflow.deform.stratigraphy.StratiGridPatchBuilder;
import fr.ifp.kronosflow.deform.stratigraphy.StratigraphyPropertyComputer;
import fr.ifp.kronosflow.dem.deformation.DEMDeformation;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerStep;
import fr.ifp.kronosflow.geoscheduler.property.TimePropertyUpdater;
import fr.ifp.kronosflow.incubator.descriptors.LineSetImportJSON;
import fr.ifp.kronosflow.kernel.extensions.IExtension;
import fr.ifp.kronosflow.kernel.extensions.ray.RayExtension;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.kernel.polyline.explicit.InfinitePolyline;
import fr.ifp.kronosflow.kernel.property.IPropertyAccessor;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.LineSet;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.algo.ComputeContact;
import fr.ifp.kronosflow.model.builder.PatchBuilderFactory;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.factory.ModelFactory.ComplexityType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.filters.SectionFactory;
import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.model.property.EnumProperty;
import fr.ifp.kronosflow.model.property.ImagePropertyAccessor;
import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import fr.ifp.kronosflow.utils.KronosContext;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifp.kronosflow.utils.TempDir;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import stratifx.application.griding.bl2d.BL2DParamInfo;
import stratifx.application.caller.EventUIAction;
import stratifx.application.griding.compact2d.Compact2DParamInfo;
import stratifx.application.fxcontrollers.ParamInfo;
import stratifx.application.griding.grid2d.Grid2DParamInfo;
import stratifx.application.properties.PropertiesUIAction;
import stratifx.application.properties.TimePropertyComputer;
import stratifx.application.properties.XYPropertyComputer;
import stratifx.application.solvers.FEMSolverParamInfo;
import stratifx.application.griding.stratigrid.StratiGridParamInfo;
import stratifx.application.griding.triangulation.TriangulationParamInfo;
import stratifx.application.webkine.WebSolver;
import stratifx.model.filters.JSONSectionLoad;
import stratifx.model.filters.JSONSectionSave;
import stratifx.model.wrappers.GeologicLibraryWrapper;
import stratifx.model.wrappers.PatchWrapper;
import stratifx.model.wrappers.PolylineWrapper;
import stratifx.model.wrappers.SectionWrapper;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StratiFXService implements
        IUIController,
        IControllerService {


    DisplayedObjects displayedObjects;

    //GeoschedulerSection section;

    private Stage primaryStage;

    Map<IUIController.Type, IUIController> controllers;

    static public StratiFXService instance;

    static {
        instance = new StratiFXService();
    }

    protected StratiFXService() {

        displayedObjects = new DisplayedObjects();

        controllers = new HashMap<IUIController.Type, IUIController>();

        KronosContext.registerClass(Section.class, GeoschedulerSection.class);
        KronosContext.registerClass(PolyLine.class, ExplicitPolyLine.class);
        KronosContext.registerClass(IExtension.class, RayExtension.class);
        KronosContext.registerClass(IPropertyAccessor.class, ImagePropertyAccessor.class);
        
        
        PatchBuilderFactory.registerBuilder("StratiGrid", StratiGridPatchBuilder.class);

        PropertyController.registerBuilder(EnumProperty.XY, new XYPropertyComputer.Builder());
        PropertyController.registerBuilder(EnumProperty.TIME, new TimePropertyComputer.Builder());
        PropertyController.registerBuilder(EnumProperty.POROSITY, new PorosityComputer.Builder());
        PropertyController.registerBuilder(EnumProperty.STRATIGRAPHY, new StratigraphyPropertyComputer.Builder());
        //PropertyController.registerBuilder("Poisson", new PoissonComputer.Builder());
        //PropertyController.registerBuilder("Surface", new SurfacePropertyComputer.Builder() );

        //PropertyController.registerBuilder("Strate Orientation", new StrateOrientationComputer.Builder() );
        //PropertyController.registerBuilder("SolidSurface", new SolidSurfaceComputer.Builder() );
        WrapperFactory.registerClass(Section.class, SectionWrapper.class);
        WrapperFactory.registerClass(GeoschedulerSection.class, SectionWrapper.class);
        WrapperFactory.registerClass(ExplicitPatch.class, PatchWrapper.class);
        WrapperFactory.registerClass(ExplicitPolyLine.class, PolylineWrapper.class);
        WrapperFactory.registerClass(InfinitePolyline.class, PolylineWrapper.class);
        WrapperFactory.registerClass(GeologicLibrary.class, GeologicLibraryWrapper.class);

        ParamInfo.register( new BL2DParamInfo() );
        ParamInfo.register( new Compact2DParamInfo() );
        ParamInfo.register( new StratiGridParamInfo() );
        ParamInfo.register( new TriangulationParamInfo() );
        ParamInfo.register( new Grid2DParamInfo() );
        ParamInfo.register( new FEMSolverParamInfo() );

        
        DeformationFactory.getInstance().register( DeformationFactory.Kind.DEFORMATION, "DEM", DEMDeformation.class);

        DeformationFactory.getInstance().register( DeformationFactory.Kind.SOLVER, "WebKine", WebSolver.class);

        LOGGER.setLogger(new StratiFXLogger());

    }

    public KinObject getDisplayedObjects(){
        return displayedObjects;
    }

    public Section getSection() {
        return displayedObjects.findObject(Section.class);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("StratiFX");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void registerController(IUIController.Type type, IUIController controller) {
        controllers.put(type, controller);
    }

    public void removeController(Type type) {
        controllers.remove(type);
    }

    public IUIController getController(Type type) {
        return controllers.get(type);
    }

    public void broadCastAction(UIAction action) {

        //handled by service first.
        if (handleAction(action)) {
            //action is eaten.
            return;
        }

        //then forward message to all controllers.
        for (IUIController controller : controllers.values()) {
            if (controller.handleAction(action)) {
                //action is eaten.
                return;
            }
        }
    }

    public void broadCastAction(int actionType) {
        broadCastAction(new UIAction(actionType));
    }

    public void fireAction(IUIController.Type type, UIAction action) {
        controllers.get(type).handleAction(action);
    }

    public void fireAction(IUIController.Type type, int action) {
        controllers.get(type).handleAction(new UIAction(action));
    }

    @Override
    public boolean handleAction(UIAction action) {
        switch (action.getType()) {
            case UIAction.OPEN:
                return handleOpen();

            case UIAction.IMPORT_LINES:
                return handleImportLines();

            case UIAction.SAVE:
                return handleSave();

            case UIAction.LOAD:
                return handleLoad();

            case UIAction.PROPERTIES:
                return handlePropertiesAction((PropertiesUIAction) action);

            case UIAction.EVENT:
                return handleEventAction((EventUIAction)action);

            default:
                break;
        }

        return false;
    }


    private boolean handleEventAction( EventUIAction action ){

        GeoschedulerSection section = (GeoschedulerSection)getSection();
        IControllerEvent<?> event = action.getData();
        if (event instanceof UpdateEvent) {
            saveSection();
            new TimePropertyUpdater(section).update();
            ComputeContact.recalculateAllPatches(section.getPatchLibrary());
        }

        return false; //to further handle event.
    }

    private boolean handlePropertiesAction(PropertiesUIAction action) {

        if (action.getData() == EnumProperty.ELONGATION) {
            return false;
        }

        PropertyControllerCaller caller = new PropertyControllerCaller(this);
        caller.setPropertyKey(action.getData());
        caller.compute();
        caller.publish();

        return false;

    }


    private boolean handleSave() {

        Section section = getSection();
        if ( null == section ){
            return false;
        }

        JSONSectionSave saver = new JSONSectionSave();
        saver.setSection(section);
        saver.setFileName(TempDir.getTmpAbsolutePath() + "section.json");
        saver.execute();

        return true;
    }


    private boolean handleLoad() {

        GeoschedulerSection section = null;

        JSONSectionLoad loader = new JSONSectionLoad();
        loader.setFileName(TempDir.getTmpAbsolutePath() + "section.json");
        if ( loader.execute() ){
            section = (GeoschedulerSection)loader.getSection();
        }

        if ( null != section ) {

            ComputeContact.recalculateAllPatches(section.getPatchLibrary());

            section.getGeoscheduler().getRoot().getWrapper().save(section);

            setCurrentSection(section);

        }

        //false to propagate event action
        return false;
    }


    private boolean handleImportLines() {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return false;
        }

        LineSetImportJSON importer = new LineSetImportJSON(file.getAbsolutePath());
        if ( importer.execute() ) {

            LineSet lineSet = importer.getLineSet();

            LOGGER.info(String.format("read %d lines from file", lineSet.size()), getClass());

            LineSet oldLineSet = displayedObjects.findObject(LineSet.class);
            if ( oldLineSet != null ){
                displayedObjects.remove(oldLineSet);
            }
            displayedObjects.add(lineSet);
        }

        return false;
    }

    private boolean handleOpen() {


        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return false;
        }

        String filename = file.getAbsolutePath();
        String basename = filename.substring(0, filename.lastIndexOf('.'));

        LOGGER.debug("load " + basename, this.getClass());

        GeoschedulerSection section = new GeoschedulerSection();
        section.setName(basename);

        Style style = section.getStyle();
        style.cloneData( GParameters.getStyle() );

        SceneStyle sceneStyle = new SceneStyle(style);
        sceneStyle.setGridType("None");
        sceneStyle.setComplexityType(ComplexityType.SINGLE);


        Map<String, String> unitMap = SectionFactory.createBorders(filename, section);

        File f = new File(basename + ".xml");
        if (f.exists() && !f.isDirectory()) {
            SectionFactory.createDummyUnit(basename + ".xml", section, unitMap);
        } else {
            f = new File(basename + ".unit");
            if (f.exists() && !f.isDirectory()) {
                SectionFactory.createDummyUnit(basename + ".unit", section, unitMap);
            }
        }

        //TODO create root. 
        section.getGeoscheduler().getRoot().getWrapper().save(section);

        setCurrentSection(section);

        //false to propagate event action
        return false;
    }

    @Override
    public void preHandle(ControllerEventList eventList) {
        LOGGER.debug("preHandle ", getClass());
    }

    @Override
    public void handleEvents(ControllerEventList eventList) {

        Map< EnumEventAction, IControllerEvent<?>> summary = new HashMap<>();

        eventList.forEach((event) -> {
            summary.put(event.getEventAction(), event);
        });

        for (IControllerEvent<?> event : summary.values()) {
            LOGGER.debug("handle " + event.getClass().getSimpleName(), getClass());
            broadCastAction( new EventUIAction(event) );
        }

    }

    @Override
    public List<String> deactivateActiveManipulators() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void activateManipulators(Collection<String> handlerIds) {
        // TODO Auto-generated method stub
    }

    private void saveSection() {

        GeoschedulerSection section = (GeoschedulerSection)getSection();

        GeoschedulerStep step = section.getGeoscheduler().getCurrent();
        IWrapper<Section> wrappedSection = step.getWrapper();
        wrappedSection.save(section);
    }


    private void setCurrentSection( Section section ){
        GeoschedulerSection oldSection = (GeoschedulerSection)getSection();
        if ( null != oldSection ){
            displayedObjects.remove(oldSection);
        }
        displayedObjects.add(section);
    }


    class DisplayedObjects extends KinObject {
        public DisplayedObjects(){
            super();
        }
    }


}
