package stratifx.application.graph;

import fr.ifp.kronosflow.model.graph.Vertex;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class TriangleCell extends CellFX {

    public TriangleCell( Vertex vertex ) {
        super(vertex);

        double width = 50;
        double height = 50;

        Polygon view = new Polygon( width / 2, 0, width, height, 0, height);

        view.setStroke(Color.RED);
        view.setFill(Color.RED);

        setView( view);

    }

}