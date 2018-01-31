package stratifx.application.views;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.controllers.events.UpdateEvent;
import fr.ifp.kronosflow.controllers.property.PropertyEvent;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.application.caller.PatchDeleteEvent;
import stratifx.application.caller.UnitRemovedItem;
import stratifx.application.plot.GViewsFactory;
import stratifx.canvas.graphics.GObject;

import java.util.ArrayList;
import java.util.List;

public class GSectionView extends GView {


    public GSectionView() {
        super();
    }

    @Override
    public void setModel(Object object) {

        setUserData(object);

        Section section = (Section) object;
        setName(section.getName());

        createPatchViews();

        PatchLibrary patchLib = section.getPatchLibrary();
        GView view = GViewsFactory.createView(patchLib.getPaleobathymetry());
        add(view);

    }

    @Override
    public void modelChanged(IControllerEvent<?> event) {

        if (event instanceof PatchDeleteEvent) {
            handleDeleteEvent( (PatchDeleteEvent)event);
        }
        else if (event instanceof UpdateEvent) {
            handleUpdateEvent( (UpdateEvent)event);
        }
        else if ( event instanceof PropertyEvent){
            handlePropertyEvent((PropertyEvent)event);
        }

    }

    private void handleDeleteEvent(PatchDeleteEvent deleteEvent ){
        LOGGER.debug("handleDeleteEvent", getClass());
        UnitRemovedItem item = deleteEvent.getObject();
        for (Patch patch : item.getPatches()) {
            for (GObject gObject : getChildren()) {
                if (gObject instanceof GView) {
                    GView view = (GView) gObject;
                    if ( ( view.getModel() instanceof Patch ) &&
                            ((Patch)view.getModel()).getUID().equals(patch.getUID()) ) {
                        remove(view);
                        break;
                    }
                }
            }

        }
    }

    private void handleUpdateEvent( UpdateEvent updateEvent ){
        if (updateEvent.getUpdateType() != UpdateEvent.Type.COMPUTE) {
            LOGGER.debug("handleUpdateEvent", getClass());
            removePatchViews();
            createPatchViews();
            redraw();
        }
    }

    private void handlePropertyEvent( PropertyEvent propEvent ){
        List<GObject> children = getChildren();
        for (GObject gObject : children) {
            if (gObject instanceof GView) {
                ((GView) gObject).modelChanged(propEvent);
            }
        }
    }


    public void createPatchViews() {

        Section section = getSection();

        PatchLibrary patchLib = section.getPatchLibrary();

        // Create a graphic object
        for (Patch patch : patchLib.getPatches()) {
            GView view = GViewsFactory.createView(patch);
            add(view);
        }
    }

    public void removePatchViews() {
        List<GView> views = new ArrayList<>();

        List<GObject> children = getChildren();
        for (GObject gObject : children) {
            if (gObject instanceof GView) {
                GView view = (GView) gObject;
                if (view.getModel() instanceof Patch) {
                    views.add(view);
                }
            }
        }

        children.removeAll(views);

    }

    public Section getSection() {
        return (Section) getUserData();
    }


}



