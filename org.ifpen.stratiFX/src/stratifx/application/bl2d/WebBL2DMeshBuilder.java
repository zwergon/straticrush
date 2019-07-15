package stratifx.application.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.polyline.Interval;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.mesh.builder.IMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.BL2D.geometry.Geometry;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifpen.kine.BL2D.Env;
import fr.ifpen.kine.client.BL2DClient;
import fr.ifpen.kine.client.SimulationClient;

import fr.ifpen.kine.encoder.DefaultEncoderService;
import fr.ifpen.kine.mesh.Cell;
import fr.ifpen.kine.mesh.Mesh;
import fr.ifpen.kine.mesh.Node;
import fr.ifpen.kine.mesh.Topology;
import fr.ifpen.kine.process.ProcessState;
import fr.ifpen.kine.process.StateBit;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebBL2DMeshBuilder implements IMeshBuilder {

    private Geometry webCreateGeometry(Long simulationId, Patch patch){

        //preprocess Geometry
        GeometryMapper geometryMapper = new GeometryMapper();
        Geometry geometry = geometryMapper.geomFromMesh2D(patch);
        geometry.setName("geometry from StratiFX");
        geometry.setSimulationId(simulationId);

        return geometry;

    }

    private Env webCreateEnv(Long simulationId){

        //preprocess Env
        EnvMapper envMapper = new EnvMapper();
        Env env = envMapper.defaultEnv();
        env.setName("default env from StratiFX");
        env.setSimulationId(simulationId);

        return env;
    }

    private Env webCreate2Env(Long simulationId){

        //preprocess Env
        Env env = webCreateEnv(simulationId);
        env.setSmooth(6);

        return env;
    }

    public Mesh2D createMesh(Patch patch) {

        Long simulationId = SimulationClient.createSimulationNow("bl2d");

        if(simulationId > 0){
            return webCreateMesh(simulationId, webCreateGeometry(simulationId,patch), webCreate2Env(simulationId));
        }

        return  null;

    }

    private Mesh2D webCreateMesh(Long simulationId, Geometry geometry, Env env){

        if (simulationId > 0) {

            if (!BL2DClient.write(geometry)) {
                LOGGER.error("unable to write geometry", getClass());
                return null;
            }

            if (!BL2DClient.write(env)) {
                LOGGER.error("unable to write env", getClass());
                return null;
            }


            if (BL2DClient.launchSimulation(simulationId) <= 0) {
                LOGGER.error("unable to launch the simulation", getClass());
                return null;
            }

        }

        StateBit stateBit = new StateBit();
        ProcessState state = null;
        try {

            do {

                Thread.sleep(200);

                state = BL2DClient.getState(simulationId);
                stateBit.setState(state.getState());
            } while( !state.isEnded(stateBit) );
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }


        if  ( state != null ) {

            if ( state.getDiagnosis() != ProcessState.Diagnosis.ERROR ) {

                Mesh resMesh = (Mesh) BL2DClient.getResults(simulationId);
                if ( null != resMesh ) {
                    return fromMesh(resMesh);
                }
                else {
                    LOGGER.error("unable to retrieve mesh", getClass());
                }
            }
            else {
                LOGGER.error("WebBL2DMeshBuilder quit with diagnosis " + state.getDiagnosis() + " and error code " + state.getErrorCode(), getClass());
            }


        }

        return null;
    }

    private BL2DMesh fromMesh(Mesh mesh){
        DefaultEncoderService enc = new DefaultEncoderService();
        BL2DMesh bl2dMesh = new BL2DMesh();
        Map<String, Node> nodes = mesh.getNodesMap();

        for(Map.Entry<String, Node> n: nodes.entrySet()){
            double[] pt = n.getValue().getPoint3D();
            double[] p = {pt[0],pt[1]};
            fr.ifp.kronosflow.kernel.polyline.Node nf = new fr.ifp.kronosflow.kernel.polyline.Node(p);
            nf.setUID(enc.cellIndex(n.getKey()));
            bl2dMesh.addNode(nf);
        }

        Map<String, Cell> cells = mesh.getCellsMap();
        for(Map.Entry<String, Cell> c : cells.entrySet()){
            Cell cp = c.getValue();
            if(cp.getTopology() == Topology.QUAD){
                UID u1 = new UID(enc.cellIndex(cp.getIds()[0]));
                UID u2 = new UID(enc.cellIndex(cp.getIds()[1]));
                UID u3 = new UID(enc.cellIndex(cp.getIds()[2]));
                UID u4 = new UID(enc.cellIndex(cp.getIds()[3]));
                fr.ifp.kronosflow.mesh.Quad quad = new fr.ifp.kronosflow.mesh.Quad(u1,u2,u3,u4);
                bl2dMesh.addCell(quad);
            }
            if(cp.getTopology() == Topology.TRGL){
                UID u1 = new UID(enc.cellIndex(cp.getIds()[0]));
                UID u2 = new UID(enc.cellIndex(cp.getIds()[1]));
                UID u3 = new UID(enc.cellIndex(cp.getIds()[2]));
                fr.ifp.kronosflow.mesh.Triangle trgl = new fr.ifp.kronosflow.mesh.Triangle(u1,u2,u3);
                bl2dMesh.addCell(trgl);
            }
        }

        return bl2dMesh;
    }

    @Override
    public Mesh2D createMesh(List<Point2D> pts) { return null;}

    @Override
    public Mesh2D createMesh(Set<fr.ifp.kronosflow.kernel.polyline.Node> nodes) { return null;}
}
