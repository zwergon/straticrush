package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.builder.IMeshBuilder;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.BL2D.Env;
import fr.ifpen.kine.BL2D.geometry.Geometry;
import fr.ifpen.kine.client.BL2DClient;
import fr.ifpen.kine.encoder.DefaultEncoderService;
import fr.ifpen.kine.mesh.Cell;
import fr.ifpen.kine.mesh.Mesh;
import fr.ifpen.kine.mesh.Node;
import fr.ifpen.kine.mesh.Topology;
import fr.ifpen.kine.process.ProcessState;
import fr.ifpen.kine.process.StateBit;
import stratifx.application.main.GParameters;
import stratifx.application.webkine.WebServiceStyle;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BL2DMeshBuilder implements IMeshBuilder{

    BL2DClient bl2DClient;

    public BL2DMeshBuilder() {
        WebServiceStyle serviceStyle = new WebServiceStyle(GParameters.getInstanceStyle());

        bl2DClient = new BL2DClient(serviceStyle.getBaseUrl());
    }

    private Geometry webCreate2Geometry(Long simulationId, List<Point2D> point2DS){

        //preprocess Geometry
        GeometryMapper geometryMapper = new GeometryMapper();
        Geometry geometry = geometryMapper.geom2FromMesh2D(point2DS);
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

    protected Env webCreate2Env(Long simulationId){

        //preprocess Env
        Env env = webCreateEnv(simulationId);
        env.setSmooth(6);

        return env;
    }

    protected Mesh2D webCreateMesh(Long simulationId, Geometry geometry, Env env){


        if (simulationId > 0) {

            if (!bl2DClient.write(geometry)) {
                LOGGER.error("unable to write geometry", getClass());
                return null;
            }

            if (!bl2DClient.write(env)) {
                LOGGER.error("unable to write env", getClass());
                return null;
            }


            if (bl2DClient.launchSimulation(simulationId) <= 0) {
                LOGGER.error("unable to launch the simulation", getClass());
                return null;
            }

        }

        StateBit stateBit = new StateBit();
        ProcessState state = null;
        try {

            do {

                Thread.sleep(200);

                state = bl2DClient.getState(simulationId);
                stateBit.setState(state.getState());
            } while( !state.isEnded(stateBit) );
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }


        if  ( state != null ) {

            if ( state.getDiagnosis() != ProcessState.Diagnosis.ERROR ) {
                Mesh resMesh = (Mesh) bl2DClient.getResults(simulationId);
                if ( null != resMesh ) {
                    return fromMesh(resMesh);
                }
                else {
                    LOGGER.error("unable to retrieve mesh", getClass());
                }
            }
            else {
                LOGGER.error("BL2DMeshBuilder quit with diagnosis " + state.getDiagnosis() + " and error code " + state.getErrorCode(), getClass());
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

    public Mesh2D createMesh(List<Point2D> pts){

        WebServiceStyle serviceStyle = new WebServiceStyle(GParameters.getInstanceStyle());
        bl2DClient.login( serviceStyle.getLogin(), serviceStyle.getPassWord());

        Long simulationId = bl2DClient.createSimulationNow("bl2d");

        if(simulationId > 0){
            return webCreateMesh(simulationId, webCreate2Geometry(simulationId,pts), webCreate2Env(simulationId));
        }
        return null;
    }

    @Override
    public Mesh2D createMesh(Set<fr.ifp.kronosflow.kernel.polyline.Node> nodes) { return null;}

}
