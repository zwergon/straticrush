package stratifx.application.graph;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Line;

public class EdgeFX extends Group {

    protected Node source;
    protected Node target;

    Line line;

    public EdgeFX(CellFX src, CellFX dest) {

        this.source = src.getView();
        this.target = dest.getView();

        line = new Line();


        line.startXProperty().bind( source.layoutXProperty());
        line.startYProperty().bind( source.layoutYProperty());

        line.endXProperty().bind( target.layoutXProperty());
        line.endYProperty().bind( target.layoutYProperty());

        getChildren().add( line);

    }

    /*public CellFX getSource() {
        return source;
    }

    public CellFX getTarget() {
        return target;
    }*/

}