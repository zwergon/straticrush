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
class PersistablePolyline {
    
    long lineID;
    
    boolean closed;
    
    //for ICurviPoint
    long[]   curviIDs;
    double[] curviPositions;
    

    //for Nodes
    long[]   nodesIDs;
    double[] nodesPositions;
    
    public long getLineID() {
        return lineID;
    }

    public void setLineID(long lineID) {
        this.lineID = lineID;
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

    public double[] getNodesPositions() {
        return nodesPositions;
    }

    public void setNodesPositions(double[] nodesPositions) {
        this.nodesPositions = nodesPositions;
    }
       
}
