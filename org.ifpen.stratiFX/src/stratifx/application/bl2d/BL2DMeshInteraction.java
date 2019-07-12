package stratifx.application.bl2d;


import fr.ifp.kronosflow.deform.scene.Scene;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import stratifx.application.bl2d.WebBL2DMeshBuilder;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.application.views.GMesh;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.interaction.GMouseEvent;

import java.util.HashMap;
import java.util.List;

public class BL2DMeshInteraction extends AMesh2DInteraction {


    public BL2DMeshInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new BL2DPatchMeshBuilder();
    }
}
