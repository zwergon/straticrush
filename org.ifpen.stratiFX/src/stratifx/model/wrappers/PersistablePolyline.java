/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

/**
 *
 * @author lecomtje
 */
class PersistablePolyline extends AbstractPersisted {

 
    boolean closed;

    //for ICurviPoint
    long[]   curviIDs;
    double[] curviValues;
    double[] curviPositions;

    //for Nodes
    long[] nodesIDs;

  
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
