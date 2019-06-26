package stratifx.application.webkine;

import fr.ifp.kronosflow.deform.deformation.DeformationStyle;
import fr.ifp.kronosflow.deform.deformation.MeshSolver;
import fr.ifp.kronosflow.deform.deformation.constraint.DeformLink;
import fr.ifp.kronosflow.kernel.polyline.Node;
import fr.ifp.kronosflow.kernel.warp.Displacement;
import fr.ifp.kronosflow.kernel.warp.NodeDisplacement;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.client.*;
import fr.ifpen.kine.constraint.ConstraintSet;
import fr.ifpen.kine.constraint.Displacements;
import fr.ifpen.kine.constraint.Material;
import fr.ifpen.kine.mesh.Mesh;
import fr.ifpen.kine.mesh.Region;
import fr.ifpen.kine.process.ProcessState;
import fr.ifpen.kine.process.StateBit;

import java.util.Collection;

public class WebSolver extends MeshSolver {


    boolean withDeleteSession = true;

    static {
        WebKineClient.setBaseUrl("http://irlin326287:8090/api");
        //WebKineClient.setBaseUrl("http://10.8.3.209:9000/api");
    }

    @Override
    public boolean solve(Collection<DeformLink> nodeLinks) {
        Long simulationId = SimulationClient.createSimulationNow("aster");

        Mesh2D mesh2d = getMesh();

        if ( simulationId > 0 ) {

            //preprocess Mesh
            MeshMapper meshMapper = new MeshMapper();
            Mesh mesh = meshMapper.fromMesh2D(mesh2d);
            mesh.setName("mesh generated by stratiFX");
            mesh.setSimulationId(simulationId);


            //preprocess Constraint -> --- <B>Mesh is modified</B> ----
            ConstraintMapper constraintMapper = new ConstraintMapper(mesh);
            ConstraintSet constraintSet = constraintMapper.fromDeformLinks(nodeLinks);
            constraintSet.setName("stratiFX");
            constraintSet.setSimulationId(simulationId);

            DeformationStyle deformationStyle = new DeformationStyle(deformation.getStyle());
            Material defaultMaterial = new Material();
            defaultMaterial.setName("mate001");
            defaultMaterial.setNu(deformationStyle.getPoisson());
            defaultMaterial.setYoung(deformationStyle.getYoung());

            Region materialRegion = mesh.findRegionByName("Material");
            defaultMaterial.setRegionId( materialRegion.getId() );

            constraintSet.addMaterial(defaultMaterial);

            if ( !AsterClient.write(mesh) ){
                return false;
            }

            if ( !AsterClient.write(constraintSet) ){
                return false;
            }


            if ( AsterClient.launchSimulation(simulationId) <= 0 ){
                return false;
            }
        }


        StateBit stateBit = new StateBit();
        ProcessState processState = null;
        try {

            do {

                Thread.sleep(200);

                processState = SimulationClient.getState(simulationId);
                stateBit.setState(processState.getState());
            } while( !processState.isEnded(stateBit) );
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if  ( processState != null ) {

            if ( processState.getDiagnosis() != ProcessState.Diagnosis.ERROR ) {

                Displacements displacements = (Displacements)SimulationClient.getResults(simulationId);
                if ( null != displacements ) {
                    handleDisplacements(mesh2d, displacements);
                }
                else {
                    LOGGER.error("unable to retrieve displacements", getClass());
                }

                if ( withDeleteSession ) {
                    //SimulationClient.deleteSimulation(simulationId);
                }

                return true;
            }
            else {
                LOGGER.error("WebSolver quit with diagnosis " + processState.getDiagnosis() + " and error code " + processState.getErrorCode(), getClass());
            }


        }



        return false;
    }

    private void handleDisplacements(Mesh2D mesh2d, Displacements displacements) {
        synchronized (getController()) {
            for (Displacement disp : warp.getDisplacements()) {
                NodeDisplacement nodeDisplacement = (NodeDisplacement) disp;

                Node node = (Node) mesh2d.getNode(nodeDisplacement.getNodeId());
                double[] pos = node.getPosition();
                double[] du = displacements.getDu(((NodeDisplacement) disp).getNodeId().getId());

                if (null != du) {
                    disp.setTarget(new double[]{pos[0] + du[0], pos[1] + du[1]});
                }
            }
        }
    }

}
