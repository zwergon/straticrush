package stratifx.application.webkine;

import fr.ifp.jdeform.deformation.constraint.DeformLink;
import fr.ifp.kronosflow.geometry.Geometry;
import fr.ifp.kronosflow.mesh.NodeLink;
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


    static public ConstraintSet fromDeformLinks(Collection<DeformLink> links ){

        IEncoderService encoderService = new DefaultEncoderService();

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.setSolver(new Solver());

        Displacements displacements = new Displacements();
        displacements.setName("displ001");

        Set<UID> fixedUIDS = new HashSet<>();

        for( DeformLink deformLink : links ){

            switch( deformLink.getType()){
                case DISPLACEMENT:
                    extractDisplacement(encoderService, displacements, deformLink, fixedUIDS);
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
                case POINTPAIR:
                    extractPointPair(encoderService, dofLinks, deformLink, fixedUIDS);
                    break;
                default:
                    break;
            }
        }

        constraintSet.addConstraint(dofLinks);

        return constraintSet;
    }

    private static void extractDisplacement(
            IEncoderService encoderService,
            Displacements displacements,
            DeformLink deformLink,
            Set<UID> fixedUids )
    {
        NodeLink nodeLink = deformLink.getLink();

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

    private static void extractPointPair(
            IEncoderService encoderService,
            DofLinks dofLinks,
            DeformLink deformLink,
            Set<UID> fixedUids )
    {
        NodeLink nodeLink = deformLink.getLink();

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
