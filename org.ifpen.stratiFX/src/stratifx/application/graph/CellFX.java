package stratifx.application.graph;

import fr.ifp.kronosflow.model.graph.Vertex;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import javafx.scene.Node;

public class CellFX  implements IHandle {

    Vertex vertex;

    Node view;

    CellFXType type;


    protected CellFX( CellFXType type, Vertex vertex){
        this.type = type;
        this.vertex = vertex;
    }

    public void setView(Node view) {
        this.view = view;
    }

    public Node getView() {
        return this.view;
    }

    @Override
    public UID getUID() {
        return vertex.getUID();
    }

    public CellFXType getType() {
        return type;
    }
}