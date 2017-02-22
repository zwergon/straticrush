/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.wrapper.IPersisted;
import fr.ifp.kronosflow.uids.UID;

/**
 *
 * @author lecomtje
 */
public class PersistablePatch extends AbstractPersisted {

    private long unitId;

    private int facies;

    private String name;

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

    
    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public int getFaciesId() {
        return facies;
    }

    public void setFaciesId(int facies) {
        this.facies = facies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setBorder(PersistablePolyline border) {
        this.border = border;
    }
   

}
