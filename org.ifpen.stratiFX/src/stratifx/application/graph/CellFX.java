package stratifx.application.graph;

import java.util.ArrayList;
import java.util.List;

import fr.ifp.kronosflow.model.graph.Vertex;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class CellFX extends Pane implements IHandle {

    Vertex vertex;

    Node view;

    protected CellFX( Vertex vertex ){
        this.vertex = vertex;
    }

    public void setView(Node view) {
        this.view = view;
        getChildren().add(view);
    }

    public Node getView() {
        return this.view;
    }

    @Override
    public UID getUID() {
        return vertex.getUID();
    }
}