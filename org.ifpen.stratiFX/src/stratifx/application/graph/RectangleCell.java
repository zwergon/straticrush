package stratifx.application.graph;

import fr.ifp.kronosflow.model.graph.Vertex;
import fr.ifp.kronosflow.uids.UID;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleCell extends CellFX {

    public RectangleCell(Vertex vertex) {
        super(vertex);

        Rectangle view = new Rectangle(50, 50);

        view.setStroke(Color.DODGERBLUE);
        view.setFill(Color.DODGERBLUE);

        setView(view);

    }

}
