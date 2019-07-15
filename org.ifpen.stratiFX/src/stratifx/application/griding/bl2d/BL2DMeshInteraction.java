package stratifx.application.griding.bl2d;


import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.canvas.graphics.GScene;

public class BL2DMeshInteraction extends AMesh2DInteraction {


    public BL2DMeshInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new BL2DPatchMeshBuilder();
    }
}
