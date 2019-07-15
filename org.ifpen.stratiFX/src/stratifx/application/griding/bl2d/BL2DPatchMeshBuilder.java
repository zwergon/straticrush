package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;

import java.util.List;

public class BL2DPatchMeshBuilder extends WebBL2DMeshBuilder implements IPatchMeshBuilder  {

    Patch selected;

    @Override
    public void initialize(Patch patch, List<Point2D> pts) {
        this.selected = patch;
    }

    @Override
    public Mesh2D createMesh(List<Point2D> pts) {
        if ( null != selected ){
            return createMesh(selected);
        }

        return null;
    }
}
