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

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;

/**
 *
 * @author lecomtje
 */
public class PersistablePatch extends AbstractPersisted {

    private long unitId;

    private int facies;

    IPersisted border;

    /**
     * ids of the featureIntervals of the patch
     */
    private long[] featureIntervalIds = new long[0];

    /**
     * s1,s2 of the intervals
     */
    private double[] intervalsS1S2 = new double[0];

    /**
     * ids of the feature of the interval
     */
    private long[] boundaryfeaturesId = new long[0];

    public PersistablePatch() {
    }

    public PersistablePatch(Patch patch) {
        super(patch, patch.getName());
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public int getFaciesId() {
        return facies;
    }

    public void setFaciesId(long facies) {
        this.facies = (int)facies;
    }

    public long[] getFeatureIntervalIds() {
        return featureIntervalIds;
    }

    public void setFeatureIntervalIds(long[] featureIntervalIds) {
        this.featureIntervalIds = featureIntervalIds;
    }

    public double[] getIntervalsS1S2() {
        return intervalsS1S2;
    }

    public void setIntervalsS1S2(double[] intervalsS1S2) {
        this.intervalsS1S2 = intervalsS1S2;
    }

    public long[] getBoundaryfeaturesId() {
        return boundaryfeaturesId;
    }
    
    public int indexOfFeaturesId( UID uid ){
        for( int i=0; i<boundaryfeaturesId.length; i++ ){
            if ( boundaryfeaturesId[i] == uid.getId() ){
                return i;
            }
        }
        
        return -1;
    }

    public void setBoundaryfeaturesId(long[] boundaryfeaturesId) {
        this.boundaryfeaturesId = boundaryfeaturesId;
    }

    public IPersisted getBorder() {
        return border;
    }

    public void setBorder(IPersisted border) {
        this.border = border;
    }
   

}
