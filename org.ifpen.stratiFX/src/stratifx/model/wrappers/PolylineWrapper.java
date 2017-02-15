/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lecomtje
 */
public class PolylineWrapper implements IWrapper<PolyLine>{
    
    PersistablePolyline persistedPolyline = new PersistablePolyline();

    PolylineWrapper() {
    }
    
     PolylineWrapper( PersistablePolyline persistedPolyline ) {
         this.persistedPolyline = persistedPolyline;
    }

    @Override
    public boolean load(PolyLine toLoad) {
        
        toLoad.clearNodes();
        toLoad.clearPoints();
        
        toLoad.setUID( persistedPolyline.getLineID() ) ;
        toLoad.setClosed( persistedPolyline.isClosed() );
        
        long[] nodesIds   = persistedPolyline.getNodesIDs();
        double[] nodesPos = persistedPolyline.getNodesPositions();
        for( int i=0; i<nodesIds.length; i++ ){
            toLoad.addNode(
                    new Node(
                            new double[]{
                                nodesPos[2 * i],
                                nodesPos[2 * i + 1]
                            },
                            new UID(nodesIds[i]))
            );
        }
        
        
        long[] curviIDs    = persistedPolyline.getCurviIDs();
        double[] curviPos  = persistedPolyline.getCurviPositions();
        for (int i = 0; i < nodesIds.length; i++) {
            ICurviPoint cp =toLoad.addPoint(
                            new double[]{
                                curviPos[2 * i],
                                curviPos[2 * i + 1]
                            }
            );
            cp.setUID( curviIDs[i] );
        }
        
        
        
        
        return true;
    }

    @Override
    public boolean save(PolyLine toSave) {
        
        persistedPolyline.setLineID( toSave.getUID().getId() );
        persistedPolyline.setClosed( toSave.isClosed() );
        
        //TODO 
        if ( toSave instanceof IMeshProvider ){
            LOGGER.warning("MeshPolyline are not handled", getClass());
            return false;
        }
        
          //save nodes
        List<IHandle> handles = new ArrayList<>();
        toSave.getAllNodes(handles);
        long[] nodesIds = new long[handles.size()];
        double[] nodesPos = new double[handles.size() * 2];

        for (int i = 0; i < handles.size(); i++) {
            Node node = (Node) handles.get(i);
            nodesIds[i] = node.getUID().getId();
            double[] position = node.getPosition();
            nodesPos[2 * i] = position[0];
            nodesPos[2 * i + 1] = position[1];
        }
        persistedPolyline.setNodesIDs(nodesIds);
        persistedPolyline.setNodesPositions(nodesPos);
            
        
        //save curvipoints
        List<ICurviPoint> curvis = toSave.getPoints();
        long[] curviIDs = new long[curvis.size()];
        double[] curviPositions = new double[2*curvis.size()];
        
        double[] position = new double[2];
        for( int i=0; i< curvis.size(); i++ ){
            ICurviPoint cp = curvis.get(i);
            toSave.getPosition(cp, position);
            
            curviIDs[i] = cp.getUID().getId();
            curviPositions[2*i] = position[0];
            curviPositions[2*i+1] = position[1];
        }
        persistedPolyline.setCurviIDs(curviIDs);
        persistedPolyline.setCurviPositions(curviPositions);
        
        
        
        return true;
        
    }
    
}
