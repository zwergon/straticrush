package stratifx.application.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;

import java.util.List;

public class BL2DPatchMeshBuilder extends WebBL2DMeshBuilder implements IPatchMeshBuilder  {
    @Override
    public void initialize(Patch patch, List<Point2D> pts) {
    }
}
