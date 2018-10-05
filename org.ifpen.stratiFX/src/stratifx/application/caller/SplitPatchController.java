package stratifx.application.caller;

import fr.ifp.kronosflow.controllers.AbstractChangeController;
import fr.ifp.kronosflow.controllers.events.AbstractControllerEvent;
import fr.ifp.kronosflow.controllers.events.EnumEventAction;
import fr.ifp.kronosflow.controllers.events.EnumEventType;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.geometry.RectD;
import fr.ifp.kronosflow.kernel.polyline.ICurviPoint;
import fr.ifp.kronosflow.kernel.polyline.IPolyline;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.kernel.polyline.PolyLineGeometry;
import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.*;
import fr.ifp.kronosflow.model.algo.ComputeContact;
import fr.ifp.kronosflow.model.algo.ComputeFeature;
import fr.ifp.kronosflow.model.algo.Discretizer;
import fr.ifp.kronosflow.model.algo.KronosHelper;
import fr.ifp.kronosflow.model.factory.ModelFactory;
import fr.ifp.kronosflow.model.factory.PatchInfo;
import fr.ifp.kronosflow.model.geology.BoundaryFeature;
import fr.ifp.kronosflow.model.geology.StratigraphicEvent;
import fr.ifp.kronosflow.model.geology.StratigraphicUnit;
import fr.ifp.kronosflow.model.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class SplitPatchController extends AbstractChangeController {

    private static double MIN_LENGTH_REF = 500;

    public Pair<Patch, Patch> splitPatch(Patch oldPatch,
                                         PolyLine polyline,
                                         StratigraphicEvent stratigraphicEvent ) {


        PatchLibrary library = oldPatch.getPatchLibrary();

        Section section = library.getSection();

        // discretize our created points
        RectD box = library.getBoundingBox();
        double length = getLengthRef(box.width(), box.height());
        Discretizer.discretize(polyline, length);

        List<Point2D> createdPoints = polyline.getPoints2D();
        IPolyline border = oldPatch.getBorder();
        PolyLineGeometry geom = new PolyLineGeometry(border);


        // reverse our list if necessary
        Point2D first = createdPoints.get(0);
        Point2D last = createdPoints.get(createdPoints.size() - 1);
        createSplitPointsInNeighbourPatchs(oldPatch, first);
        createSplitPointsInNeighbourPatchs(oldPatch, last);
        if (geom.getCurviPointOnPolyline(first).getCurvilinear() > geom
                .getCurviPointOnPolyline(last).getCurvilinear()) {
            Collections.reverse(createdPoints);
        }

        double start = geom.getCurviPointOnPolyline(createdPoints.get(0)).getCurvilinear();
        double end = geom.getCurviPointOnPolyline(createdPoints.get(createdPoints.size() - 1))
                .getCurvilinear();

        Patch newPatch1 = ModelFactory.createPatch(new PatchInfo());
        newPatch1.setName("Split" + oldPatch.getName() + "A");

        Patch newPatch2 = ModelFactory.createPatch(new PatchInfo());
        newPatch2.setName("Split" + oldPatch.getName() + "B");

        List<Point2D> ptList1 = new ArrayList<>();
        List<Point2D> ptList2 = new ArrayList<>();

        boolean splitMark = false;
        for (ICurviPoint cpt : oldPatch.getBorder().getPoints()) {
            if (splitMark == false) {
                if (cpt.getCurvilinear() > start) {
                    splitMark = true;
                    ptList1.addAll(createdPoints);
                    if (cpt.getCurvilinear() < end) {
                        ptList2.add(border.getPosition(cpt));
                    }
                    else {
                        ptList1.add(border.getPosition(cpt));
                    }
                }
                else {
                    ptList1.add(border.getPosition(cpt));
                }
            }
            else {
                if (cpt.getCurvilinear() < end) {
                    ptList2.add(border.getPosition(cpt));
                }
                else {
                    ptList1.add(border.getPosition(cpt));
                }
            }
        }

        Collections.reverse(createdPoints);
        ptList2.addAll(createdPoints);

        newPatch1.createBorder(ptList1);
        newPatch2.createBorder(ptList2);

        switchPatch(oldPatch, newPatch1, newPatch2);


        setNewFeature(newPatch1,
                stratigraphicEvent,
                createdPoints.get(createdPoints.size() - 1),
                createdPoints.get(0));

        setNewFeature(newPatch2,
                stratigraphicEvent,
                createdPoints.get(0),
                createdPoints.get(createdPoints.size() - 1));

        section.getFeatures().addGeologicFeature(stratigraphicEvent);

        assignUnit( newPatch1, newPatch2,
                    oldPatch,
                    stratigraphicEvent);

        return new Pair<>(newPatch1, newPatch2);
    }

    private void assignUnit(Patch newPatch1, Patch newPatch2, Patch oldPatch, StratigraphicEvent stratigraphicEvent) {

        StratigraphicUnit oldUnit = oldPatch.getGeologicFeaturesByClass(StratigraphicUnit.class);

        boolean top = false;

        for (PatchInterval pi : newPatch1.getIntervals()) {
            if (pi instanceof FeatureGeolInterval) {
                if (((FeatureGeolInterval) pi).getInterval().getFeature().equals(oldUnit.getTop())) {
                    top = true;
                }
            }
        }

        //new patch1 is above
        if ( top ){
            newPatch1.setFeature(stratigraphicEvent.getAbove());
            newPatch2.setFeature(stratigraphicEvent.getBelow());
        }
        else {

            newPatch1.setFeature(stratigraphicEvent.getBelow());
            newPatch2.setFeature(stratigraphicEvent.getAbove());
        }

    }

    private void switchPatch(Patch oldPatch, Patch newPatch1, Patch newPatch2) {

        PatchLibrary patchLib = oldPatch.getPatchLibrary();

        // Deleting oldPatch

        ComputeContact.removeContactsOfPatch(oldPatch, patchLib, patchLib.getContactGraph());
        patchLib.removePatch(oldPatch);


        // Adding new Patches

        ComputeFeature compFeat1 = new ComputeFeature(newPatch1);
        ComputeFeature compFeat2 = new ComputeFeature(newPatch2);

        // assign oldpatch's features
        for (PatchInterval pi : oldPatch.getIntervals()) {
            if (pi instanceof FeatureGeolInterval) {
                PolyLine polyline = new ExplicitPolyLine();
                polyline.initialize(pi.getPolyline().getPoints2D());
                BoundaryFeature feat = pi.getInterval().getFeature();
                compFeat1.execute(polyline, feat);
                compFeat2.execute(polyline, feat);
            }
        }

        // put them in library
        patchLib.addPatch(newPatch1);
        newPatch1.setPatchLibrary(patchLib);
        ComputeContact.recalculatePatch(newPatch1, patchLib);

        patchLib.addPatch(newPatch2);
        newPatch2.setPatchLibrary(patchLib);
        ComputeContact.recalculatePatch(newPatch2, patchLib);

    }

    private void setNewFeature(Patch newPatch, BoundaryFeature feature, Point2D start,
            Point2D end) {
        IPolyline border = newPatch.getBorder();
        PolyLineGeometry geom = new PolyLineGeometry(border);
        ICurviPoint cp1 = geom.getCurviPointOnPolyline(start);
        ICurviPoint cp2 = geom.getCurviPointOnPolyline(end);

        // add the new interval
        FeatureGeolInterval interval = newPatch.addFeatureGeolInterval(cp1, cp2, feature);
    }

    public void reassemblePatch(Patch oldPatch, Pair<Patch, Patch> createdPatches,
            Section section) {

        PatchLibrary patchLib = createdPatches.getFirst().getPatchLibrary();

        // Deleting created Patches
        List<Patch> patchList = new ArrayList<>(2);
        patchList.add(createdPatches.getFirst());
        patchList.add(createdPatches.getSecond());
        for (Patch patch : patchList) {
            ComputeContact.removeContactsOfPatch(patch, patchLib, patchLib.getContactGraph());
            patchLib.removePatch(patch);
        }
        AbstractControllerEvent<?> event = new PatchDeleteEvent(EnumEventAction.REMOVE,
                EnumEventType.PATCH, new UnitRemovedItem(patchList, null));
        push(event);

        // Adding the old Patch
        patchList = new ArrayList<>(1);
        patchList.add(oldPatch);

        patchLib.addPatch(oldPatch);
        oldPatch.setPatchLibrary(patchLib);

        // features definition
        ComputeContact.recalculatePatch(oldPatch, patchLib);
        KronosHelper.calculateBorders(patchLib);

        event = new PatchAddEvent(EnumEventAction.ADD, EnumEventType.PATCH, patchList);
        push(event);

    }

    /**
     * Creates a point in the neighbour patch of the specified patch.
     * 
     * @param patch
     * @param point
     */
    public void createSplitPointsInNeighbourPatchs(Patch patch, Point2D point) {
        List<ContactInterval> contactIntervals = patch.getIntervals().stream()
                .filter(object -> object instanceof ContactInterval)
                .map(object -> (ContactInterval) object).collect(Collectors.toList());

        for (ContactInterval contactInterval : contactIntervals) {
            PolyLineGeometry polyLineGeometry = new PolyLineGeometry(contactInterval.getInterval());
            if (polyLineGeometry.isPointOnPolyline(point.getPosition())) {
                ContactInterval otherContactInterval = contactInterval.getContact()
                        .getOther(contactInterval);

                PolyLine otherPatchBorder = otherContactInterval.getPatch().getBorder();
                PolyLineGeometry pgeom = new PolyLineGeometry(otherPatchBorder);
                ICurviPoint cp = pgeom.getCurviPointOnPolyline(point);
                otherPatchBorder.addPoint(cp);
            }
        }
    }

    public static double getLengthRef(double width, double height) {
        double length = Math.max(Math.abs(width), Math.abs(height)) / 30.;
        return Math.min(length, MIN_LENGTH_REF);
    }

}
