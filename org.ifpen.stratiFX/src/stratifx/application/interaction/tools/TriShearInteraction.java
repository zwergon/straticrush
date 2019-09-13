package stratifx.application.interaction.tools;

import fr.ifp.kronosflow.deform.scene.FaultMS;
import fr.ifp.kronosflow.model.Patch;
import stratifx.application.interaction.MasterSlaveInteraction;
import stratifx.application.views.GPolyline;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class TriShearInteraction  extends MasterSlaveInteraction {

    private class GFault extends GObject {

        public GFault(FaultMS faultMS){
            setUserData(faultMS);
            GPolyline gFault = new GPolyline(faultMS.getSupport());

            GStyle style = new GStyle();
            style.setForegroundColor(GColor.red);
            style.setLineWidth(2);
            gFault.setStyle(style);

            add(gFault);
        }
    }

    public TriShearInteraction(GScene scene) {
        super(scene);
    }

    protected void handleMousePress(GMouseEvent event) {

        if (event.button != GMouseEvent.BUTTON_1) {
            return;
        }

        Patch patch = getSelectedPatch(event.x, event.y);

        if ( null == patch ){
            return;
        }

        scene = createPatchScene(patch);

        FaultMS faultMS = (FaultMS)selectFault(event.x, event.y);

        if ( null != faultMS ){

            GFault gFault = new GFault(faultMS);
            gObjects.put(G_FAULT, gFault);
            gscene.add(gFault);

            gFault.redraw();

        }


    }


}
