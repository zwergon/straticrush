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
package stratifx.model.persistable;

import fr.ifp.kronosflow.kernel.polyline.PolyLine;

/**
 *
 * @author lecomtje
 */
public class PersistablePolyline extends AbstractPersisted {

 
    boolean closed;

    //for ICurviPoint
    long[]   curviIDs;
    double[] curviValues;
    double[] curviPositions;

    //for Nodes
    long[] nodesIDs;

    public PersistablePolyline() {
    }

    public PersistablePolyline(PolyLine handle) {
        super(handle, null);
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public long[] getCurviIDs() {
        return curviIDs;
    }

    public void setCurviIDs(long[] curviIDs) {
        this.curviIDs = curviIDs;
    }

    public double[] getCurviPositions() {
        return curviPositions;
    }

    public void setCurviPositions(double[] curviPositions) {
        this.curviPositions = curviPositions;
    }

    public void setNodesIDs(long[] nodesIDs) {
        this.nodesIDs = nodesIDs;
    }

    public long[] getNodesIDs() {
        return nodesIDs;
    }

    public double[] getCurviValues() {
        return curviValues;
    }

    public void setCurviValues(double[] curviValues) {
        this.curviValues = curviValues;
    }

}
