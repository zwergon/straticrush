package stratifx.application.webkine;

import fr.ifp.jdeform.deformation.constraint.DeformLink;
import fr.ifp.kronosflow.geometry.Geometry;
import fr.ifp.kronosflow.mesh.NodeLink;
import fr.ifp.kronosflow.mesh.regions.RegionLink;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.constraint.*;
import fr.ifpen.kine.encoder.DefaultEncoderService;
import fr.ifpen.kine.encoder.IEncoderService;
import fr.ifpen.kine.mesh.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConstraintMapper {

    IEncoderService encoderService;

    ConstraintSet constraintSet;

    Mesh mesh;

    Set<String> fixedNodes = new HashSet<>();

    Set<String> slaveNodes = new HashSet<>();

    public ConstraintMapper( Mesh mesh ){

        encoderService = new DefaultEncoderService();

        constraintSet = new ConstraintSet();
        constraintSet.setSolver(new Solver());

        this.mesh = mesh;
    }


    public ConstraintSet fromDeformLinks(Collection<DeformLink> links ){

        Displacements displacements = new Displacements();
        displacements.setName("displ001");



        for( DeformLink deformLink : links ){

            switch( deformLink.getType()){
                case DISPLACEMENT:
                    extractDisplacement(displacements, deformLink);
                    break;
                default:
                    break;
            }
        }

        constraintSet.addConstraint(displacements);

        DofLinks dofLinks = new DofLinks();
        dofLinks.setName("ddl001");
        for( DeformLink deformLink : links ){

            switch( deformLink.getType()){
                case POINT_PAIR:
                    extractPointPair(dofLinks, deformLink);
                    break;

                case MS_REGIONS:
                    extractContacts(deformLink);
                default:
                    break;
            }
        }

        if ( dofLinks.isValid() ) {
            constraintSet.addConstraint(dofLinks);
        }

        return constraintSet;
    }


    private void extractContacts( DeformLink deformLink  ){

        Solver solver = constraintSet.getSolver();
        solver.setKind(Solver.Kind.NON_LINEAR);

        Contact contact = new Contact();
        contact.setName("contact");

        RegionLink regionLink = (RegionLink)deformLink.getLink();

        String masterId = encoderService.regionId(Region.Type.CELL, regionLink.getMasterId().getId());
        int masterSize = removeCommonNodes(fixedNodes, masterId);

        String slaveId = encoderService.regionId(Region.Type.CELL, regionLink.getSlaveId().getId());
        removeCommonNodes(fixedNodes, slaveId);
        int slaveSize = removeCommonNodes(slaveNodes, slaveId);

        slaveNodes.addAll( mesh.findRegion(slaveId).getNodeIds(mesh) );

        contact.setMasterId(masterId);
        contact.setSlaveId(slaveId);

        if ( (masterSize > 0) && ( slaveSize > 0 ) ) {
            constraintSet.addContact(contact);
        }
        else {
            LOGGER.warning("contact for master/slave " +  masterId + "/" + slaveId + " is empty->remove it", getClass());
        }
    }



    /**
     * remove from {@link Region} all {@link Cell} that contain a reference
     * to one {@link Node} in the given  {@link Set} of {@link Node}.
     * @param regionId the {@link String} id of the region to test
     * @return number of {@link Node} remaining in the {@link Region}
     */
    private int removeCommonNodes(Set<String> nodeSet, String regionId) {

        Region region = mesh.findRegion( regionId );
        if ( null == region ){
            return 0;
        }

        //if a cell is in touch with one fixedNode, mark cell to be removed.
        Set<String> cellsToRemove = new HashSet<>();
        for( String idCell : region.getIds() ){
            Cell cell = mesh.getCell( idCell );
            for( String nodeId : cell.getIds() ){

                if ( nodeSet.contains(nodeId) ){
                    cellsToRemove.add(idCell);
                    break;
                }
            }
        }
        region.removeIds( cellsToRemove );

        return region.getIds().size();
    }




    private void extractDisplacement(
            Displacements displacements,
            DeformLink deformLink )
    {
        NodeLink nodeLink = (NodeLink)deformLink.getLink();

        Collection<UID> uids = nodeLink.getLinkedNodes();
        if ( uids.size() == 1 ){
            UID uid = uids.iterator().next();



            double w = nodeLink.getWeight(uid);
            if ( !Geometry.isEqual(w, 1) ){
                LOGGER.warning( "single node imposed displacement with a weird weight parameter", ConstraintMapper.class );
            }

            String nodeId = encoderService.cellId(Topology.NODE, uid.getId());

            fixedNodes.add(nodeId);

            double[] k = nodeLink.getConstant();

            Displacement displacement = new Displacement();
            displacement.setSupportId(nodeId);
            displacement.setDisplacement(Constraint.Direction.DX, k[0] );
            displacement.setDisplacement(Constraint.Direction.DY, k[1] );
            displacements.addDisplacement( displacement );
        }
    }

    private void extractPointPair(
            DofLinks dofLinks,
            DeformLink deformLink )
    {
        NodeLink nodeLink = (NodeLink)deformLink.getLink();

        double[] k = nodeLink.getConstant();
        DofLink dofLink = new DofLink();
        dofLink.addDir(Constraint.Direction.DX, k[0]);
        dofLink.addDir(Constraint.Direction.DY, k[1]);

        boolean valid = true;
        for( UID uid : nodeLink.getLinkedNodes()){

            String nodeId = encoderService.cellId(Topology.NODE, uid.getId());
            if ( fixedNodes.contains(nodeId)) {
                valid = false;
                break;
            }

            DofLink.Dof dof = new DofLink.Dof();
            dof.id = nodeId;
            dof.w = nodeLink.getWeight(uid);
            dofLink.addDof(dof);
        }

        if ( valid ) {
            dofLinks.addDofLink(dofLink);
        }

    }
}
