package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import fr.ifpen.kine.BL2D.geometry.Geometry;
import fr.ifpen.kine.client.SimulationClient;

import java.util.List;

public class BL2DPatchMeshBuilder extends BL2DMeshBuilder implements IPatchMeshBuilder  {

    Patch selected;

    @Override
    public void initialize(Patch patch, List<Point2D> pts) {
        this.selected = patch;
    }

    @Override
    public Mesh2D createMesh(List<Point2D> pts) {
        if ( null != selected ){
            return createPatchMesh(selected);
        }

        return null;
    }

    private Geometry webCreateGeometry(Long simulationId, Patch patch){

        //preprocess Geometry
        GeometryMapper geometryMapper = new GeometryMapper();
        Geometry geometry = geometryMapper.geomFromMesh2D(patch);
        geometry.setName("geometry from StratiFX");
        geometry.setSimulationId(simulationId);

        return geometry;

    }

    private Mesh2D createPatchMesh(Patch patch) {

        Long simulationId = bl2DClient.createSimulationNow("bl2d");

        if (simulationId == null) {
            return null;
        }

        return webCreateMesh(simulationId, webCreateGeometry(simulationId,patch), webCreate2Env(simulationId));

    }
}
