package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.mesh.*;
import fr.ifp.kronosflow.uids.UID;

import java.util.*;

public class BL2DMesh extends Mesh2D {

    public BL2DMesh(){}

    @Override
    public MeshEditor getEditor() {
        return null;
    }

    @Override
    public List<UID> getNodeNeighbors(UID coId) {
        return new ArrayList<UID>(0);
    }

    @Override
    public List<UID> getCellNeighbors(UID coId) {
        return new ArrayList<UID>();
    }
}
