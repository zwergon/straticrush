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
import fr.ifpen.kine.mesh.Topology;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConstraintMapper {

    IEncoderService encoderService;

    ConstraintSet constraintSet;

    public ConstraintMapper(){

        encoderService = new DefaultEncoderService();

        constraintSet = new ConstraintSet();
        constraintSet.setSolver(new Solver());

    }


    public ConstraintSet fromDeformLinks(Collection<DeformLink> links ){





        Displacements displacements = new Displacements();
        displacements.setName("displ001");

        Set<UID> fixedUIDS = new HashSet<>();

        for( DeformLink deformLink : links ){

            switch( deformLink.getType()){
                case DISPLACEMENT:
                    extractDisplacement(displacements, deformLink, fixedUIDS);
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
                    extractPointPair(dofLinks, deformLink, fixedUIDS);
                    break;

                case MS_REGIONS:
                    extractContacts(deformLink, fixedUIDS );
                default:
                    break;
            }
        }

        constraintSet.addConstraint(dofLinks);

        return constraintSet;
    }


    private void extractContacts(
            DeformLink deformLink,
            Set<UID> uids
    ){
        Contact contact = new Contact();
        contact.setName("contact");

        RegionLink regionLink = (RegionLink)deformLink.getLink();


    }

    private void extractDisplacement(
            Displacements displacements,
            DeformLink deformLink,
            Set<UID> fixedUids )
    {
        NodeLink nodeLink = (NodeLink)deformLink.getLink();

        Collection<UID> uids = nodeLink.getLinkedNodes();
        if ( uids.size() == 1 ){
            UID uid = uids.iterator().next();

            fixedUids.add(uid);

            double w = nodeLink.getWeight(uid);
            if ( !Geometry.isEqual(w, 1) ){
                LOGGER.warning( "single node imposed displacement with a weird weight parameter", ConstraintMapper.class );
            }

            double[] k = nodeLink.getConstant();

            Displacement displacement = new Displacement();
            displacement.setSupportId(encoderService.cellId(Topology.NODE,uid.getId()));
            displacement.setDisplacement(Constraint.Direction.DX, k[0] );
            displacement.setDisplacement(Constraint.Direction.DY, k[1] );
            displacements.addDisplacement( displacement );
        }
    }

    private void extractPointPair(
            DofLinks dofLinks,
            DeformLink deformLink,
            Set<UID> fixedUids )
    {
        NodeLink nodeLink = (NodeLink)deformLink.getLink();

        double[] k = nodeLink.getConstant();
        DofLink dofLink = new DofLink();
        dofLink.addDir(Constraint.Direction.DX, k[0]);
        dofLink.addDir(Constraint.Direction.DY, k[1]);

        boolean valid = true;
        for( UID uid : nodeLink.getLinkedNodes()){
            if ( fixedUids.contains( uid)) {
                valid = false;
                break;
            }

            DofLink.Dof dof = new DofLink.Dof();
            dof.id = encoderService.cellId(Topology.NODE,uid.getId());
            dof.w = nodeLink.getWeight(uid);
            dofLink.addDof(dof);
        }

        if ( valid ) {
            dofLinks.addDofLink(dofLink);
        }

    }
}
