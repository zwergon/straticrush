/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.explicit.ExplicitPoint;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.wrapper.IPersisted;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;
import java.util.List;

/**
 *
 * @author lecomtje
 */
public class PolylineWrapper implements IWrapper<PolyLine> {

    PersistablePolyline persistedPolyline;

    public PolylineWrapper() {
    }

    @Override
    public boolean load(PolyLine toLoad) {

        if (null == persistedPolyline) {
            return false;
        }
        
        LOGGER.warning("load Polyline", getClass() );


        toLoad.clearNodes();
        toLoad.clearPoints();

        toLoad.setUID(persistedPolyline.getUID());
        toLoad.setClosed(persistedPolyline.isClosed());


        long[] curviIDs = persistedPolyline.getCurviIDs();
        long[] nodesIDS = persistedPolyline.getNodesIDs();
        double[] curviPos = persistedPolyline.getCurviPositions();
        double[] curviValues = persistedPolyline.getCurviValues();
        for (int i = 0; i < curviIDs.length; i++) {
            ExplicitPoint ep = (ExplicitPoint)toLoad.addPoint(
                    new double[]{
                        curviPos[2 * i],
                        curviPos[2 * i + 1]
                    }
            );
            ep.setUID(curviIDs[i]);
            ep.setCurvilinear( curviValues[i] );
            ep.setType(ICurviPoint.CoordType.EXISTING);
            ep.getNode().setUID( nodesIDS[i] );
            
        }

        return true;
    }

    @Override
    public boolean save(PolyLine toSave) {
        
        LOGGER.warning("save Polyline", getClass() );

        if (!(toSave instanceof ExplicitPolyLine)) {
            LOGGER.error("this is not an ExplicitPolyline", getClass());
            return false;
        }

        if (null == persistedPolyline) {
            persistedPolyline = new PersistablePolyline();
        }

        persistedPolyline.setLineID(toSave.getUID().getId());
        persistedPolyline.setClosed(toSave.isClosed());

        //save curvipoints
        List<ICurviPoint> curvis = toSave.getPoints();
        long[] curviIDs = new long[curvis.size()];
        long[] nodesIds = new long[curvis.size()];
        double[] curviValues = new double[curvis.size()];
        double[] curviPositions = new double[2 * curvis.size()];

        double[] position = new double[2];
        for (int i = 0; i < curvis.size(); i++) {
            ExplicitPoint cp = (ExplicitPoint) curvis.get(i);
            toSave.getPosition(cp, position);

            curviIDs[i] = cp.getUID().getId();
            nodesIds[i] = cp.getNode().getUID().getId();
            curviValues[i] = cp.getCurvilinear();
            curviPositions[2 * i] = position[0];
            curviPositions[2 * i + 1] = position[1];
        }
        persistedPolyline.setCurviIDs(curviIDs);
        persistedPolyline.setNodesIDs(nodesIds);
        persistedPolyline.setCurviValues(curviValues);
        persistedPolyline.setCurviPositions(curviPositions);

        return true;

    }

    @Override
    public void setPersisted(IPersisted persisted) {
        persistedPolyline = (PersistablePolyline) persisted;
    }

    @Override
    public IPersisted getPersisted() {
        return persistedPolyline;
    }
    
   

}
