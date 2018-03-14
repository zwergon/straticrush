package stratifx.application.graph;

import fr.ifp.kronosflow.model.graph.Vertex;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class ContactCell extends CellFX {

    public ContactCell(Vertex vertex ) {
        super(vertex);

        Rectangle view = new Rectangle( -5, -5, 10, 10);

        view.setStroke(Color.RED);
        view.setFill(Color.RED);

        setView( view);

    }

}