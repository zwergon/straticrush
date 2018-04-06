package stratifx.application.webkine;

import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.Edge;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.Triangle;
import fr.ifp.kronosflow.mesh.regions.RegionOneD;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifpen.kine.encoder.DefaultEncoderService;
import fr.ifpen.kine.encoder.IEncoderService;
import fr.ifpen.kine.mesh.Mesh;
import fr.ifpen.kine.mesh.Region;
import fr.ifpen.kine.mesh.Topology;

import java.util.HashSet;
import java.util.Set;

public class MeshMapper {


    Mesh mesh;

    IEncoderService encoderService;


    public MeshMapper(){
        mesh = new Mesh();
        encoderService = new DefaultEncoderService();

    }


    public Mesh fromMesh2D(Mesh2D mesh2d){

        for(IHandle ih : mesh2d.getNodes()){
            Node node = (Node)ih;
            fr.ifpen.kine.mesh.Node mNode = new fr.ifpen.kine.mesh.Node();
            String mId = encoderService.cellId(Topology.NODE, ih.getUID().getId());
            mNode.setId(mId);
            mNode.setPoint3D( node.x(), node.y(), 0.0 );
            mesh.addNode( mId, mNode);
        }

        for( IHandle ih : mesh2d.getCells() ){
            Cell cell = (Cell)ih;
            fr.ifpen.kine.mesh.Cell mCell = null;
            if ( cell instanceof Triangle ) {
                mCell = new fr.ifpen.kine.mesh.Triangle();
                mCell.setIds( new String[]{
                        encoderService.cellId(Topology.NODE, cell.getNode(0).getId()),
                        encoderService.cellId(Topology.NODE, cell.getNode(1).getId()),
                        encoderService.cellId(Topology.NODE, cell.getNode(2).getId())
                });
            }

            mesh.addCell( encoderService.cellId(Topology.TRGL, ih.getUID().getId()), mCell);
        }

        //Creates a Region with all cells for material

        UID uid = new UID();
        Region materialRegion = mesh.findOrCreateRegion( encoderService.regionId(Region.Type.CELL, uid.getId()));
        materialRegion.setName("Material");
        materialRegion.setIds( mesh.getCellIds() );
        mesh.addRegion(materialRegion);

        for (fr.ifp.kronosflow.mesh.regions.Region region : mesh2d.getRegionDB().getRegions() ){
            switch( region.getRegionInfo().getDimension() ){
                case 0:
                    break;
                case 1:
                    mesh.addRegion( fromRegionOneD( (RegionOneD)region ) );
                    break;
                case 2:
                    break;
            }
        }

        return mesh;
    }


    private Region fromRegionOneD( RegionOneD regionOneD ){
        Region region = mesh.findOrCreateRegion( encoderService.regionId(Region.Type.CELL, regionOneD.getUID().getId()) );
        region.setName( regionOneD.getName() );

        Set<String> ids = new HashSet<>();
        for( Edge edge : regionOneD.getEdges() ){
            fr.ifpen.kine.mesh.Edge kEdge = new fr.ifpen.kine.mesh.Edge();
            kEdge.setId(encoderService.cellId(Topology.EDGE, edge.getUID().getId()));

            kEdge.setIds( new String[]{
                    encoderService.cellId(Topology.NODE, edge.getNode(0).getId()),
                    encoderService.cellId(Topology.NODE, edge.getNode(1).getId())
            });

            ids.add(kEdge.getId());

            mesh.addCell(kEdge.getId(), kEdge);
        }

        region.setIds(ids);

        return region;
    }



}
