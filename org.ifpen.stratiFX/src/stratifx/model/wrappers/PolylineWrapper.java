/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPoint;
import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.kernel.polyline.ICurviPoint;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;
import stratifx.model.persistable.PersistablePolyline;

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
        
        toLoad.clearNodes();
        toLoad.clearPoints();

        toLoad.setUID(persistedPolyline.getUid());
        toLoad.setClosed(persistedPolyline.isClosed());


        long[] curviIDs = persistedPolyline.getCurviIDs();
        long[] nodesIDS = persistedPolyline.getNodesIDs();
        double[] curviPos = persistedPolyline.getCurviPositions();
        double[] curviValues = persistedPolyline.getCurviValues();
        for (int i = 0; i < curviIDs.length; i++) {

            //TODO: USE ABSTRACT CLASSES to handle CELLPOINT
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
        
        if (!(toSave instanceof ExplicitPolyLine)) {
            LOGGER.error("this is not an ExplicitPolyline", getClass());
            return false;
        }

        if (null == persistedPolyline) {
            persistedPolyline = new PersistablePolyline(toSave);
        }

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
    public void setPersisted(Object persisted) {
        persistedPolyline = (PersistablePolyline) persisted;
    }

    @Override
    public Object getPersisted() {
        return persistedPolyline;
    }
    
   

}
