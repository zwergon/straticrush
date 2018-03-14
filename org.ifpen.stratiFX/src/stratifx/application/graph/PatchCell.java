package stratifx.application.graph;

import fr.ifp.kronosflow.model.graph.Vertex;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class PatchCell extends CellFX {

    public PatchCell(Vertex vertex) {
        super(vertex);

        Circle view = new Circle(25);

        view.setStroke(Color.DODGERBLUE);
        view.setFill(Color.DODGERBLUE);


        view.setOnMouseEntered(e -> view.setFill(Color.DARKGRAY));
        view.setOnMouseExited(e -> {
            view.setFill(Color.DODGERBLUE);
            LOGGER.debug("view exited", getClass());
                }
        );

        setView(view);

    }

}
